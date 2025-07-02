package io.flamingock.internal.core.task.loaded;

import io.flamingock.internal.common.core.error.FlamingockException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoadedChangeUnitUtil {

    private static final String ORDERED_FILE_REGEX = "_([^_]+)_";

    private LoadedChangeUnitUtil() {
    }



    public static String getOrderFromContentOrFileName(String changeUnitId, String orderInContent, String fileName) {
        boolean hasOrderInContent = orderInContent != null && !orderInContent.trim().isEmpty();
        Optional<String> orderFromFileName = getContentBetweenUnderscores(fileName);
        
        if (hasOrderInContent) {
            if (orderFromFileName.isPresent()) {
                if (orderInContent.equals(orderFromFileName.get())) {
                    return orderInContent;
                } else {
                    throw new FlamingockException(String.format("ChangeUnit[%s] Order mismatch: orderInContent='%s' does not match order in fileName='%s'",
                        changeUnitId, orderInContent, orderFromFileName.get()));
                }
            } else {
                return orderInContent;
            }
        } else {
            if (orderFromFileName.isPresent()) {
                return orderFromFileName.get();
            } else {
                throw new FlamingockException(String.format("ChangeUnit[%s] Order is required: neither orderInContent nor order in fileName is provided", changeUnitId));
            }
        }
    }

    private static Optional<String> getContentBetweenUnderscores(String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile(ORDERED_FILE_REGEX);
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }
}
