package io.flamingock.core.system;


import io.flamingock.core.pipeline.PreviewStage;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.task.preview.CodePreviewChangeUnit;

import java.util.Collection;
import java.util.List;

public interface SystemModule extends Comparable<SystemModule> {

    PreviewStage getStage();


    /**
     * Modules order
     */
    int getOrder();

    /**
     * @return the dependencies built by the module that are not in the application context
     */
    List<Dependency> getDependencies();

    /**
     * Indicates if this should
     */
    boolean isBeforeUserStages();

    @Override
    default int compareTo(SystemModule o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }


}
