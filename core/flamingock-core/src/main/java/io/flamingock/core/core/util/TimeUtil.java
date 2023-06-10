package io.flamingock.core.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class TimeUtil {
    private TimeUtil() {
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

    /**
     * Converts minutes to milliseconds
     *
     * @param minutes minutes to be converted
     * @return equivalent to the minutes passed in milliseconds
     */
    public static long millisToMinutes(long minutes) {
        return minutes / (60 * 1000);
    }

    public static long toMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long diffInMillis(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return toMillis(dateTime1) - toMillis(dateTime2);
    }

}
