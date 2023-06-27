package io.flamingock.core.spring;

import org.springframework.context.ApplicationContext;

public final class SpringUtil {

    private SpringUtil() {
    }

    public static String[] getActiveProfiles(ApplicationContext springContext) {
        String[] activeProfiles = springContext.getEnvironment().getActiveProfiles();
        return activeProfiles.length == 0 ? activeProfiles : new String[]{"default"};
    }
}
