package com.naatsms.payment.databind;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampToLocalDateEditor extends PropertyEditorSupport {

    public static final String START = "start";
    public static final String END = "end";

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        switch (text) {
            case START -> setValue(LocalDate.now().atStartOfDay());
            case END -> setValue(LocalDate.now().atStartOfDay().plusDays(1));
            default -> {
                Long decoded = Long.decode(text);
                Instant instant = Instant.ofEpochSecond(decoded);
                setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            }
        }
    }

}