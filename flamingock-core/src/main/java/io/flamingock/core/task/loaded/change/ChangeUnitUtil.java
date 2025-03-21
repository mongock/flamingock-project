package io.flamingock.core.task.loaded.change;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.legacy.MongockLegacyIdGenerator;
import io.flamingock.core.utils.ExecutionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeUnitUtil {
    private static final Logger logger = LoggerFactory.getLogger(CodeLoadedChangeUnit.class);


    static CodeLoadedChangeUnit getChangeUnitDescriptor(Class<?> sourceClass) {
        if (ExecutionUtils.isNewChangeUnit(sourceClass)) {
            ChangeUnit changeUnitAnnotation = sourceClass.getAnnotation(ChangeUnit.class);
            return new CodeLoadedChangeUnit(
                    changeUnitAnnotation.id(),
                    changeUnitAnnotation.order(),
                    sourceClass,
                    changeUnitAnnotation.runAlways(),
                    changeUnitAnnotation.transactional(),
                    true);
        } else if (ExecutionUtils.isLegacyChangeUnit(sourceClass)) {
            logger.warn("Detected legacy changeUnit[{}]. If it's an old changeUnit created for Mongock, it's fine. " +
                            "Otherwise, it's highly recommended us the new API[in package {}]",
                    sourceClass.getName(),
                    "io.flamingock.core.api.annotations");
            io.mongock.api.annotations.ChangeUnit changeUnitAnnotation = sourceClass.getAnnotation(io.mongock.api.annotations.ChangeUnit.class);
            return new CodeLoadedChangeUnit(
                    MongockLegacyIdGenerator.getNewId(changeUnitAnnotation.id(), changeUnitAnnotation.author()),
                    changeUnitAnnotation.order(),
                    sourceClass,
                    changeUnitAnnotation.runAlways(),
                    changeUnitAnnotation.transactional(),
                    false);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Task class[%s] should be annotate with %s",
                    sourceClass.getName(),
                    ChangeUnit.class.getName()
            ));
        }
    }
}
