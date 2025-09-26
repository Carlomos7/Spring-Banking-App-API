package com.example.banking.controller.advice;

import com.example.banking.exceptions.BankingExceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ProblemDetail problem(
      HttpStatus status,
      String title,
      String detail,
      String code,
      HttpServletRequest req,
      Map<String, ?> extra) {

    var pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    pd.setDetail(detail);
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", req.getRequestURI());
    if (code != null) pd.setProperty("code", code);
    var traceId = MDC.get("traceId");
    if (traceId != null) pd.setProperty("traceId", traceId);
    if (extra != null && !extra.isEmpty()) pd.setProperty("meta", extra);
    return pd;
  }

  // ---------- 404: Not Found ----------
  @ExceptionHandler({ CustomerNotFoundException.class, AccountNotFoundException.class, JournalNotFoundException.class })
  public ResponseEntity<ProblemDetail> handleNotFound(BankingException ex, HttpServletRequest req) {
    log.info("Not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(),
            ex.getCode().name(), req, ex.getMetadata()));
  }

  // ---------- 400: Bad Request (validation / duplicates / shape issues) ----------
  @ExceptionHandler({
      UsernameAlreadyExistsException.class,
      EmailAlreadyExistsException.class,
      ExternalReferenceAlreadyExistsException.class,
      InvalidTransactionSideException.class,
      InvalidAmountException.class,
      InvalidCurrencyCodeException.class,
      InvalidAccountKindException.class
  })
  public ResponseEntity<ProblemDetail> handleBadRequest(BankingException ex, HttpServletRequest req) {
    log.debug("Bad request: {}", ex.getMessage());
    return ResponseEntity.badRequest()
        .body(problem(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(),
            ex.getCode().name(), req, ex.getMetadata()));
  }

  // Bean Validation on DTOs (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
        .toList();
    var pd = problem(HttpStatus.BAD_REQUEST, "Validation failed",
        "One or more fields are invalid.", "VALIDATION_ERROR", req, Map.of("errors", errors));
    return ResponseEntity.badRequest().body(pd);
  }

  // Constraint violations on query params/path vars (@Validated on controller)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
    var errors = ex.getConstraintViolations().stream()
        .map(v -> Map.of("param", v.getPropertyPath().toString(), "message", v.getMessage()))
        .toList();
    var pd = problem(HttpStatus.BAD_REQUEST, "Validation failed",
        "One or more parameters are invalid.", "VALIDATION_ERROR", req, Map.of("errors", errors));
    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler({ MissingRequestHeaderException.class, MissingServletRequestParameterException.class })
  public ResponseEntity<ProblemDetail> handleMissingPieces(Exception ex, HttpServletRequest req) {
    var pd = problem(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), "MISSING_INPUT", req, Map.of());
    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler({ MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class })
  public ResponseEntity<ProblemDetail> handleTypeOrBody(Exception ex, HttpServletRequest req) {
    var pd = problem(HttpStatus.BAD_REQUEST, "Malformed request", "Request could not be parsed or types mismatched.",
        "MALFORMED_REQUEST", req, Map.of("detail", ex.getMessage()));
    return ResponseEntity.badRequest().body(pd);
  }

  // ---------- 422: Business rule violations ----------
  @ExceptionHandler({ JournalNotPendingException.class, InactiveAccountException.class, UnbalancedJournalException.class })
  public ResponseEntity<ProblemDetail> handleUnprocessable(BankingException ex, HttpServletRequest req) {
    log.debug("Business rule violation: {}", ex.getMessage());
    return ResponseEntity.unprocessableEntity()
        .body(problem(HttpStatus.UNPROCESSABLE_ENTITY, "Business rule violation", ex.getMessage(),
            ex.getCode().name(), req, ex.getMetadata()));
  }

  // ---------- 401/403 ----------
  @ExceptionHandler({ InvalidCredentialsException.class, IncorrectPasswordException.class })
  public ResponseEntity<ProblemDetail> handleUnauthorized(BankingException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(problem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(),
            ex.getCode().name(), req, ex.getMetadata()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleForbidden(AccessDeniedException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(problem(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action.",
            "FORBIDDEN", req, Map.of()));
  }

  // ---------- 409: Conflicts (DB + optimistic locking) ----------
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ProblemDetail> handleOptimisticLock(ObjectOptimisticLockingFailureException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(problem(HttpStatus.CONFLICT, "Concurrency conflict",
            "The resource was modified by another request. Please retry.",
            "CONCURRENCY_CONFLICT", req, Map.of()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(problem(HttpStatus.CONFLICT, "Data integrity violation",
            "The request conflicts with an existing resource or constraint.",
            "DATA_INTEGRITY_VIOLATION", req, Map.of("detail", ex.getMostSpecificCause().getMessage())));
  }

  // ---------- 500: Fallback ----------
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnknown(Exception ex, HttpServletRequest req) {
    log.error("Unhandled error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
            "Unexpected error occurred.", "INTERNAL_ERROR", req, Map.of()));
  }
}