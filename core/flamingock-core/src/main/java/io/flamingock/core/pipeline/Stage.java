package io.flamingock.core.pipeline;

import io.flamingock.core.task.descriptor.SortedTaskDescriptor;
import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptorBuilder;
import io.flamingock.core.task.filter.TaskFilter;
import io.flamingock.core.util.ReflectionUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the code packages, etc.
 */
public class Stage {

    private String name;
    private Collection<String> codePackages;

    private Collection<String> fileDirectories;

    private boolean parallel = false;
    private Collection<TaskFilter> filters;

    public Stage() {
    }


    public Stage(String... codePackages) {
        this(Arrays.asList(codePackages));
    }

    public Stage(Collection<String> codePackages) {
        this(codePackages, new LinkedHashSet<>(), false);
    }

    public Stage(Collection<String> codePackages, boolean parallel) {
        this(codePackages, new LinkedList<>(), parallel);

    }

    public Stage(Collection<String> codePackages, Collection<TaskFilter> filters, boolean parallel) {
        this.codePackages = new LinkedHashSet<>(codePackages);
        this.filters = filters != null ? filters : Collections.emptyList();
        this.parallel = parallel;//stageConfiguration.isParallel()
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getCodePackages() {
        return codePackages;
    }

    public void setCodePackages(Collection<String> codePackages) {
        this.codePackages = codePackages;
    }

    public Collection<String> getFileDirectories() {
        return fileDirectories;
    }

    public void setFileDirectories(Collection<String> fileDirectories) {
        this.fileDirectories = fileDirectories;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    private Collection<TaskFilter> getFilters() {
        return filters != null ? filters : Collections.emptyList();
    }

    public Stage addFilters(Collection<TaskFilter> filters) {
        Collection<TaskFilter> allFilters = this.filters != null ? new LinkedList<>(this.filters) : new LinkedHashSet<>();
        if (filters != null) allFilters.addAll(filters);
        return new Stage(this.codePackages, allFilters, this.parallel);
    }

    /*
     * Depending on the tasks inside the package or some field in the yaml, it returns a SingleLoadedStage
     * or ParallelSingleLoadedProcess.
     * <br />
     *
     * @return a sorted SingleLoadedStage, non-sorted SingleLoadedStage or a ParallelSingleLoadedProcess(non sorted),
     * depending on the task in the scanPackage,or some field in the yaml.

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
        Collection<TaskDescriptor> descriptors = getFilteredDescriptorsFromCodePackages(codePackages, getFilters());


        if (descriptors.stream().allMatch(descriptor -> descriptor instanceof SortedTaskDescriptor)) {
            //if all descriptors are sorted, we return a sorted collection
            return new LoadedStage(descriptors.stream().sorted().collect(Collectors.toList()), parallel);

        } else if (descriptors.parallelStream().anyMatch(descriptor -> descriptor instanceof SortedTaskDescriptor)) {
            //if at least one of them are sorted, but not all. An exception is thrown
            throw new IllegalArgumentException("Either all tasks are ordered or none is");

        } else {
            //if none of the tasks are sorted, an unsorted collection is returned
            return new LoadedStage(descriptors, parallel);
        }
    }

    private static Collection<TaskDescriptor> getFilteredDescriptorsFromCodePackages(Collection<String> codePackages, Collection<TaskFilter> filters) {
        ReflectionTaskDescriptorBuilder builder = ReflectionTaskDescriptorBuilder.recycledBuilder();
        return codePackages.
                stream()
                .map(ReflectionUtil::loadClassesFromPackage)
                .flatMap(Collection::stream)
                .filter(source -> filters.stream().allMatch(filter -> filter.filter(source)))
                .map(builder::setSource)
                .map(ReflectionTaskDescriptorBuilder::build)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stage stage = (Stage) o;

        return Objects.equals(name, stage.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
