package io.flamingock.core.util;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class StringUtil {
    private StringUtil() {
    }

    public static String executionId() {
        return String.format(
                "%s-%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss_SSSSSSSSS")),
                String.valueOf(UUID.randomUUID().getMostSignificantBits()).replace("-", ""));

    }



    public static String hostname() {
        return hostname("");
    }

    public static String hostname(String serviceIdentifier) {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostname = "unknown-host";
        }

        if (!isEmpty(serviceIdentifier)) {
            hostname += "-";
            hostname += serviceIdentifier;
        }
        return hostname;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
