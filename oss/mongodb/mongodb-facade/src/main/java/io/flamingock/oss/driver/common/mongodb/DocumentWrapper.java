package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.core.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public interface DocumentWrapper {


    DocumentWrapper append(String key, Object value);
    Object get(String key);
    String getString(String key);
    boolean containsKey(String key);
    Boolean getBoolean(String key);
    boolean getBoolean(Object key, boolean defaultValue);

    DocumentWrapper getWithWrapper(String key);

    int size();

    default LocalDateTime getDate(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (value.getClass().equals(Date.class)) {
            return TimeUtil.toLocalDateTime(value);

        } else if (value.getClass().equals(LocalDateTime.class)) {
            return (LocalDateTime) value;

        } if (Map.class.isAssignableFrom(value.getClass())) {
            return TimeUtil.fromIso8601((String)get("$date"));

        } else {
            throw new RuntimeException(String.format("%s cannot be cast to %s", value.getClass().getName(), Date.class.getName()));
        }
    }
}
