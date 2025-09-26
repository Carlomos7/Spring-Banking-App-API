package com.example.banking.exceptions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized definitions of all custom exceptions in the banking domain.
 * Each exception has a stable ErrorCode (for logging, ProblemDetail responses)
 * plus optional metadata (key/value) for diagnostics.
 */
public final class BankingExceptions {

    // ====================== Error Codes ======================
    public enum ErrorCode {
        // Not Found
        CUSTOMER_NOT_FOUND,
        ACCOUNT_NOT_FOUND,
        JOURNAL_NOT_FOUND,

        // Auth
        INVALID_CREDENTIALS,
        INCORRECT_PASSWORD,

        // Duplicates
        USERNAME_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS,
        EXTERNAL_REFERENCE_ALREADY_EXISTS,

        // Validation
        INVALID_TRANSACTION_SIDE,
        INVALID_AMOUNT,
        INVALID_CURRENCY_CODE,
        INVALID_ACCOUNT_KIND,

        // Business Rules
        JOURNAL_NOT_PENDING,
        INACTIVE_ACCOUNT,
        UNBALANCED_JOURNAL
    }

    /**
     * Base type for all banking-domain exceptions.
     * Carries a stable ErrorCode plus optional metadata (key/value) for diagnostics.
     */
    public static abstract class BankingException extends RuntimeException  {
        private static final long serialVersionUID = 1L;

        private final ErrorCode code;
        private final Map<String, Object> metadata;

        protected BankingException(ErrorCode code, String message) {
            super(message);
            this.code = code;
            this.metadata = Collections.emptyMap();
        }

        // With cause
        protected BankingException(ErrorCode code, String message, Throwable cause) {
            super(message, cause);
            this.code = code;
            this.metadata = Collections.emptyMap();
        }

        // With metadata
        protected BankingException(ErrorCode code, String message, Map<String, ?> metadata) {
            super(message);
            this.code = code;
            this.metadata = metadata == null || metadata.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
        }

        public ErrorCode getCode() {
            return code;
        }

        /** Additional contextual metadata (key/value) for diagnostics */
        public Map<String, Object> getMetadata() {
            return metadata;
        }

        // Convenience helper for small metadata maps
        protected static Map<String, Object> meta(String k, Object v) {
            var m = new LinkedHashMap<String, Object>(1);
            m.put(k, v);
            return m;
        }
    }

    // ====================== Resource Not Found ======================

    public static final class CustomerNotFoundException extends BankingException {
        private static final long serialVersionUID = 1L;
        public CustomerNotFoundException() {
            super(ErrorCode.CUSTOMER_NOT_FOUND, "Customer not found");
        }
        public CustomerNotFoundException(String customerId) {
            super(ErrorCode.CUSTOMER_NOT_FOUND, "Customer not found",
                  meta("customerId", customerId));
        }
        public CustomerNotFoundException(String message, Throwable cause) {
            super(ErrorCode.CUSTOMER_NOT_FOUND, message, cause);
        }
    }

    public static final class AccountNotFoundException extends BankingException {
        private static final long serialVersionUID = 1L;
        public AccountNotFoundException() {
            super(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found");
        }
        public AccountNotFoundException(String accountNumber) {
            super(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found",
                  meta("account", accountNumber));
        }
        public AccountNotFoundException(String message, Throwable cause) {
            super(ErrorCode.ACCOUNT_NOT_FOUND, message, cause);
        }
    }

    public static final class JournalNotFoundException extends BankingException {
        private static final long serialVersionUID = 1L;
        public JournalNotFoundException() {
            super(ErrorCode.JOURNAL_NOT_FOUND, "Journal not found");
        }
        public JournalNotFoundException(String journalId) {
            super(ErrorCode.JOURNAL_NOT_FOUND, "Journal not found",
                  meta("journalId", journalId));
        }
        public JournalNotFoundException(String message, Throwable cause) {
            super(ErrorCode.JOURNAL_NOT_FOUND, message, cause);
        }
    }

    // ====================== Authentication ======================

    public static final class InvalidCredentialsException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InvalidCredentialsException() {
            super(ErrorCode.INVALID_CREDENTIALS, "Invalid username/email or password");
        }
        public InvalidCredentialsException(String message, Throwable cause) {
            super(ErrorCode.INVALID_CREDENTIALS, message, cause);
        }
    }

    public static final class IncorrectPasswordException extends BankingException {
        private static final long serialVersionUID = 1L;
        public IncorrectPasswordException() {
            super(ErrorCode.INCORRECT_PASSWORD, "Current password is incorrect");
        }
        public IncorrectPasswordException(String message, Throwable cause) {
            super(ErrorCode.INCORRECT_PASSWORD, message, cause);
        }
    }

    // ====================== Duplicate Resource ======================

    public static final class UsernameAlreadyExistsException extends BankingException {
        private static final long serialVersionUID = 1L;
        public UsernameAlreadyExistsException() {
            super(ErrorCode.USERNAME_ALREADY_EXISTS, "Username already in use");
        }
        public UsernameAlreadyExistsException(String username) {
            super(ErrorCode.USERNAME_ALREADY_EXISTS, "Username already in use",
                  meta("username", username));
        }
        public UsernameAlreadyExistsException(String message, Throwable cause) {
            super(ErrorCode.USERNAME_ALREADY_EXISTS, message, cause);
        }
    }

    public static final class EmailAlreadyExistsException extends BankingException {
        private static final long serialVersionUID = 1L;
        public EmailAlreadyExistsException() {
            super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already in use");
        }
        public EmailAlreadyExistsException(String email) {
            super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already in use",
                  meta("email", email));
        }
        public EmailAlreadyExistsException(String message, Throwable cause) {
            super(ErrorCode.EMAIL_ALREADY_EXISTS, message, cause);
        }
    }

    public static final class ExternalReferenceAlreadyExistsException extends BankingException {
        private static final long serialVersionUID = 1L;
        public ExternalReferenceAlreadyExistsException() {
            super(ErrorCode.EXTERNAL_REFERENCE_ALREADY_EXISTS, "External reference already in use");
        }
        public ExternalReferenceAlreadyExistsException(String reference) {
            super(ErrorCode.EXTERNAL_REFERENCE_ALREADY_EXISTS, "External reference already in use",
                  meta("externalReference", reference));
        }
        public ExternalReferenceAlreadyExistsException(String message, Throwable cause) {
            super(ErrorCode.EXTERNAL_REFERENCE_ALREADY_EXISTS, message, cause);
        }
    }

    // ====================== Validation ======================

    public static final class InvalidTransactionSideException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InvalidTransactionSideException() {
            super(ErrorCode.INVALID_TRANSACTION_SIDE,
                  "Invalid side, must be 'debit' or 'credit'");
        }
        public InvalidTransactionSideException(String side) {
            super(ErrorCode.INVALID_TRANSACTION_SIDE,
                  "Invalid side, must be 'debit' or 'credit'",
                  meta("side", side));
        }
        public InvalidTransactionSideException(String message, Throwable cause) {
            super(ErrorCode.INVALID_TRANSACTION_SIDE, message, cause);
        }
    }

    public static final class InvalidAmountException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InvalidAmountException() {
            super(ErrorCode.INVALID_AMOUNT, "Amount must be positive");
        }
        public InvalidAmountException(long amountCents) {
            super(ErrorCode.INVALID_AMOUNT, "Amount must be positive",
                  meta("amountCents", amountCents));
        }
        public InvalidAmountException(String message, Throwable cause) {
            super(ErrorCode.INVALID_AMOUNT, message, cause);
        }
    }

    public static final class InvalidCurrencyCodeException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InvalidCurrencyCodeException() {
            super(ErrorCode.INVALID_CURRENCY_CODE, "Invalid currency code");
        }
        public InvalidCurrencyCodeException(String code) {
            super(ErrorCode.INVALID_CURRENCY_CODE, "Invalid currency code",
                  meta("currency", code));
        }
        public InvalidCurrencyCodeException(String message, Throwable cause) {
            super(ErrorCode.INVALID_CURRENCY_CODE, message, cause);
        }
    }

    public static final class InvalidAccountKindException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InvalidAccountKindException() {
            super(ErrorCode.INVALID_ACCOUNT_KIND, "Invalid account kind");
        }
        public InvalidAccountKindException(String kind) {
            super(ErrorCode.INVALID_ACCOUNT_KIND, "Invalid account kind",
                  meta("kind", kind));
        }
        public InvalidAccountKindException(String message, Throwable cause) {
            super(ErrorCode.INVALID_ACCOUNT_KIND, message, cause);
        }
    }

    // ====================== Business Rules ======================

    public static final class JournalNotPendingException extends BankingException {
        private static final long serialVersionUID = 1L;
        public JournalNotPendingException() {
            super(ErrorCode.JOURNAL_NOT_PENDING, "Cannot add entries to a non-pending journal");
        }
        public JournalNotPendingException(String journalId) {
            super(ErrorCode.JOURNAL_NOT_PENDING, "Cannot add entries to a non-pending journal",
                  meta("journalId", journalId));
        }
        public JournalNotPendingException(String message, Throwable cause) {
            super(ErrorCode.JOURNAL_NOT_PENDING, message, cause);
        }
    }

    public static final class InactiveAccountException extends BankingException {
        private static final long serialVersionUID = 1L;
        public InactiveAccountException() {
            super(ErrorCode.INACTIVE_ACCOUNT, "Cannot add entries to an inactive account");
        }
        public InactiveAccountException(String account) {
            super(ErrorCode.INACTIVE_ACCOUNT, "Cannot add entries to an inactive account",
                  meta("account", account));
        }
        public InactiveAccountException(String message, Throwable cause) {
            super(ErrorCode.INACTIVE_ACCOUNT, message, cause);
        }
    }

    public static final class UnbalancedJournalException extends BankingException {
        private static final long serialVersionUID = 1L;
        public UnbalancedJournalException() {
            super(ErrorCode.UNBALANCED_JOURNAL, "Cannot post an unbalanced journal");
        }
        public UnbalancedJournalException(String journalId) {
            super(ErrorCode.UNBALANCED_JOURNAL, "Cannot post an unbalanced journal",
                  meta("journalId", journalId));
        }
        public UnbalancedJournalException(String message, Throwable cause) {
            super(ErrorCode.UNBALANCED_JOURNAL, message, cause);
        }
        public UnbalancedJournalException(String message, Map<String, ?> metadata) {
            super(ErrorCode.UNBALANCED_JOURNAL, message, metadata);
        }
    }

    private BankingExceptions() { /* no instances */ }
}
