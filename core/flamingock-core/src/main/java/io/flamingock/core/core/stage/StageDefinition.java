package io.flamingock.core.core.stage;

import io.flamingock.core.core.audit.domain.AuditStageStatus;
import io.flamingock.core.core.task.descriptor.SortedTaskDescriptor;
import io.flamingock.core.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.core.task.descriptor.reflection.ReflectionTaskDescriptor;
import io.flamingock.core.core.task.filter.TaskFilter;
import io.flamingock.core.core.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the scanPackage, etc.
 */
public class StageDefinition {

    private final Collection<String> scanPackages;
    private Collection<TaskFilter> filters = new ArrayList<>();

    //We can pass here other sources, like yamls, etc.
    public StageDefinition(Collection<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    private static List<TaskDescriptor> getFilteredDescriptorsFromScanPackages(
            Collection<String> scanPackages,
            Collection<TaskFilter> filters) {

        return scanPackages.stream()
                .map(ReflectionUtil::loadClassesFromPackage)
                .flatMap(Collection::stream)
                .filter(source -> filters.stream().allMatch(filter -> filter.filter(source)))
                .map(source -> ReflectionTaskDescriptor.recycledBuilder().setSource(source))
                .map(ReflectionTaskDescriptor.Builder::build)
                .collect(Collectors.toList());
    }

    public StageDefinition setFilters(Collection<TaskFilter> filters) {
        this.filters = filters != null ? filters : Collections.emptyList();
        return this;
    }

    /**
     * Depending on the tasks inside the package or some field in the yaml, it returns a SingleLoadedStage
     * or ParallelSingleLoadedProcess.
     * <br />
     *
     * @return a sorted SingleLoadedStage, non-sorted SingleLoadedStage or a ParallelSingleLoadedProcess(non sorted),
     * depending on the task in the scanPackage,or some field in the yaml.
     */
    /**
     * It loads the definition from the source(scanPackage, yaml definition, etc.) and returns the LoadedStage
     * with contain the task Definition.
     * <br />
     * This method can decide up to some level, which type of process is loaded. For example in the case of 'SingleStageDefinition',
     * depending on the tasks inside the package or some field in the yaml, it returns a SingleLoadedStage or ParallelSingleLoadedProcess.
     * <br />
     *
     * @return the LoadedStage with contain the task Definition
     */
    public LoadedStage load() {
        //descriptors will potentially contain all the descriptors extracted form scanPackage, yaml, etc.
        List<TaskDescriptor> descriptors = getFilteredDescriptorsFromScanPackages(scanPackages, filters);

        Optional<TaskDescriptor> orderedDescriptorOptional = descriptors
                .stream()
                .filter(descriptor -> descriptor instanceof SortedTaskDescriptor)
                .findFirst();

        if (descriptors.stream().allMatch(descriptor -> descriptor instanceof SortedTaskDescriptor)) {
            //if all descriptors are sorted, we return a sorted collection
            return new LoadedStage(descriptors.stream().sorted().collect(Collectors.toList()));

        } else if (orderedDescriptorOptional.isPresent()) {
            //if at least one of them are sorted, but not all. An exception is thrown
            throw new IllegalArgumentException("Either all tasks are ordered or none is. Ordered task found: " + orderedDescriptorOptional.get().getId());

        } else {
            //if none of the tasks are sorted, a unsorted collection is returned
            return new LoadedStage(descriptors);
        }
    }

}
