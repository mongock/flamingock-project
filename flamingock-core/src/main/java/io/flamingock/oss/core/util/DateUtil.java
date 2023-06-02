package io.flamingock.oss.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil {
    private DateUtil() {
    }

    public static LocalDateTime fromIso8601(String dateFormatted) {
        return LocalDateTime.parse(dateFormatted, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        } else if (value.getClass().equals(Date.class)) {
            return toLocalDateTime((Date)value);
        } else if (value.getClass().equals(LocalDateTime.class)) {
            return (LocalDateTime) value;
        } else {
            throw new RuntimeException(String.format("%s cannot be cast to %s", value.getClass().getName(), Date.class.getName()));
        }
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}