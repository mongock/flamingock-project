package io.flamingock.core.core.process.single;

import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.OrderedTaskDescriptor;
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

public class SingleDefinitionProcess implements DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<String> scanPackages;
    private Collection<TaskFilter> filters = new ArrayList<>();

    //We can pass here other sources, like yamls, etc.
    public SingleDefinitionProcess(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public SingleDefinitionProcess setFilters(Collection<TaskFilter> filters) {
        this.filters = filters != null ? filters : Collections.emptyList();
        return this;
    }

    /**
     * Depending on the tasks inside the package or some field in the yaml, it returns a SingleLoadedProcess
     * or ParallelSingleLoadedProcess.
     * <br />
     * @return a sorted SingleLoadedProcess, non-sorted SingleLoadedProcess or a ParallelSingleLoadedProcess(non sorted),
     * depending on the task in the scanPackage,or some field in the yaml.
     */
    @Override
    public LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> load() {
        List<TaskDescriptor> descriptors = getDescriptorsFromScanPackage();

        Optional<TaskDescriptor> orderedDescriptorOptional = descriptors
                .stream()
                .filter(descriptor -> descriptor instanceof OrderedTaskDescriptor)
                .findFirst();

        if(descriptors.stream().allMatch(descriptor -> descriptor instanceof OrderedTaskDescriptor)) {//all ordered
            return new SingleLoadedProcess(descriptors.stream().sorted().collect(Collectors.toList()));

        } else if(orderedDescriptorOptional.isPresent()) {
            throw new IllegalArgumentException("Either all tasks are ordered or none is. Ordered task found: " + orderedDescriptorOptional.get().getId());

        } else {
            return new SingleLoadedProcess(descriptors);
        }
    }

    private List<TaskDescriptor> getDescriptorsFromScanPackage() {
        return scanPackages.stream()
                .map(ReflectionUtil::loadClassesFromPackage)
                .flatMap(Collection::stream)
                .filter(source -> filters.stream().allMatch(filter -> filter.filter(source)))
                .map(source -> ReflectionTaskDescriptor.recycledBuilder().setSource(source))
                .map(ReflectionTaskDescriptor.Builder::build)
                .collect(Collectors.toList());
    }
}
