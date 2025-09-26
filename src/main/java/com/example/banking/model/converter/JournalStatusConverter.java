package com.example.banking.model.converter;

import com.example.banking.model.JournalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts the JournalStatus enum to a lowercase string for database
 * persistence (matching the CHECK constraint in schema.sql: ('pending','posted'))
 * and back to the enum (uppercased) when reading.
 */
@Converter(autoApply = false)
public class JournalStatusConverter implements AttributeConverter<JournalStatus, String> {

    @Override
    public String convertToDatabaseColumn(JournalStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public JournalStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : JournalStatus.valueOf(dbData.toUpperCase());
    }
}
