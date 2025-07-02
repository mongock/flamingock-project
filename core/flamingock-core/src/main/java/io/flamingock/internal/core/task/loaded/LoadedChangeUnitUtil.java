package io.flamingock.internal.core.task.loaded;

import io.flamingock.internal.common.core.error.FlamingockException;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoadedChangeUnitUtil {

    // For template files: must start with _order_ (e.g., _002_whatever.yaml)
    private static final String TEMPLATE_FILE_ORDER_REGEX = "^_([^_]+)_";
    
    // For class names: must have _order_ at the beginning of the class name after package 
    // (e.g., com.mycompany.mypackage._002_mychange)
    private static final String CLASS_NAME_ORDER_REGEX = "\\._([^_]+)_[^.]*$";

    private LoadedChangeUnitUtil() {
    }

    /**
     * For TemplateLoadedChangeUnit - validates order from template file name
     */
    public static String getOrderFromContentOrFileName(String changeUnitId, String orderInContent, String fileName) {
        return validateAndGetOrder(changeUnitId, orderInContent, fileName, 
            LoadedChangeUnitUtil::getOrderFromTemplateFileName, "fileName");
    }

    /**
     * For CodeLoadedChangeUnit - validates order from class name
     */
    public static String getOrderFromContentOrClassName(String changeUnitId, String orderInContent, String className) {
        return validateAndGetOrder(changeUnitId, orderInContent, className, 
            LoadedChangeUnitUtil::getOrderFromClassName, "className");
    }

    /**
     * Common validation logic for both template files and class names
     */
    private static String validateAndGetOrder(String changeUnitId, String orderInContent, String source, 
                                            Function<String, Optional<String>> orderExtractor, 
                                            String sourceType) {
        boolean hasOrderInContent = orderInContent != null && !orderInContent.trim().isEmpty();
        Optional<String> orderFromSource = orderExtractor.apply(source);
        
        if (hasOrderInContent) {
            if (orderFromSource.isPresent()) {
                if (orderInContent.equals(orderFromSource.get())) {
                    return orderInContent;
                } else {
                    throw new FlamingockException(String.format("ChangeUnit[%s] Order mismatch: orderInContent='%s' does not match order in %s='%s'",
                        changeUnitId, orderInContent, sourceType, orderFromSource.get()));
                }
            } else {
                return orderInContent;
            }
        } else {
            if (orderFromSource.isPresent()) {
                return orderFromSource.get();
            } else {
                throw new FlamingockException(String.format("ChangeUnit[%s] Order is required: neither orderInContent nor order in %s is provided", 
                    changeUnitId, sourceType));
            }
        }
    }

    /**
     * Extracts order from template file name - must start with _order_
     */
    private static Optional<String> getOrderFromTemplateFileName(String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile(TEMPLATE_FILE_ORDER_REGEX);
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }

    /**
     * Extracts order from class name - must have _order_ at the beginning of class name after package
     */
    private static Optional<String> getOrderFromClassName(String className) {
        if (className == null) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile(CLASS_NAME_ORDER_REGEX);
        Matcher matcher = pattern.matcher(className);

        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }
}
