package com.naatsms.payment.databind;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampToLocalDateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Long decoded = Long.decode(text);
        Instant instant = Instant.ofEpochSecond(decoded);
        setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }

}
