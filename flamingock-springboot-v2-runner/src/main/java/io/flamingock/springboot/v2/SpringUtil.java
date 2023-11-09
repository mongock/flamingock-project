package io.flamingock.springboot.v2;

import org.springframework.context.ApplicationContext;

public final class SpringUtil {

    private SpringUtil() {
    }

    public static String[] getActiveProfiles(ApplicationContext springContext) {
        String[] activeProfiles = springContext.getEnvironment().getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles : new String[]{"default"};
    }
}
