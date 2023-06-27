package io.flamingock.core.spring;

import io.flamingock.core.core.task.filter.TaskFilter;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class SpringProfileFilter implements TaskFilter<AnnotatedElement> {

    private final List<String> activeProfiles;

    public SpringProfileFilter(List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }


    @Override
    public boolean filter(AnnotatedElement taskClass) {
        if (!taskClass.isAnnotationPresent(Profile.class)) {
            return true; // no-profiled changeset always matches
        }
        boolean taskHasAtLeastOneProfileApplied = false;
        String[] taskProfile = taskClass.getAnnotation(Profile.class).value();
        for (String profile : taskProfile) {
            if ((profile == null || "".equals(profile))) {
                continue;
            }
            if (isNegativeProfile(profile)) {
                if (containsNegativeProfile(activeProfiles, profile)) {
                    return false;
                }
            } else {
                taskHasAtLeastOneProfileApplied = true;
                if (activeProfiles.contains(profile)) {
                    return true;
                }
            }
        }
        return !taskHasAtLeastOneProfileApplied;
    }

    private static boolean isNegativeProfile(String profile) {
        return profile.charAt(0) == '!';
    }


    private boolean containsNegativeProfile(List<String> activeProfiles, String profile) {
        return isNegativeProfile(profile) && activeProfiles.contains(profile.substring(1));
    }

}
