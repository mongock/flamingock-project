package io.flamingock.core.pipeline;

import io.flamingock.core.task.descriptor.TaskDescriptor;
import io.flamingock.core.task.descriptor.ReflectionTaskDescriptorBuilder;
import io.flamingock.core.task.descriptor.TemplatedTaskDescriptorBuilder;
import io.flamingock.core.task.filter.TaskFilter;
import io.flamingock.core.task.navigation.navigator.StepNavigator;
import io.flamingock.template.TemplatedTaskDefinition;
import io.flamingock.core.util.FileUtil;
import io.flamingock.core.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represents the process defined by the user in the builder, yaml, etc.
 * It doesn't necessary contain directly the tasks, it can contain the code packages, etc.
 */
public class Stage {
    private static final Logger logger = LoggerFactory.getLogger(StepNavigator.class);

    private String name;
    private Collection<String> codePackages;

    private Collection<String> fileDirectories;

    private Collection<TaskFilter> filters;
    private boolean parallel = false;

    public Stage() {
    }



    public Stage(Stage prototype) {
        this.name = prototype.name;
        this.codePackages = prototype.codePackages != null ? new LinkedHashSet<>(prototype.getCodePackages()) : null;
        this.fileDirectories = prototype.fileDirectories != null ? new LinkedHashSet<>(prototype.getFileDirectories()) : null;
        this.filters = filters != null ? new LinkedHashSet<>(prototype.getFilters()) : null;
        this.parallel = prototype.parallel;
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
        if(filters != null) {
            filters.forEach(this::addFilter);
        }
        return this;
    }

    public Stage addFilter(TaskFilter filter) {
        if(filters == null) {
            filters = new LinkedHashSet<>();
        }
        filters.add(filter);
        return this;
    }

    public Stage addCodePackage(String codePackage) {
        if(codePackages == null) {
            codePackages = new LinkedHashSet<>();
        }
        codePackages.add(codePackage);
        return this;
    }

    public Stage addFileDirectory(String fileDirectory) {
        if(fileDirectories == null) {
            fileDirectories = new LinkedHashSet<>();
        }
        fileDirectories.add(fileDirectory);
        return this;
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
        Collection<TaskFilter> filters = getFilters();
        Collection<TaskDescriptor> descriptors = new ArrayList<>(getFilteredDescriptorsFromCodePackages(codePackages, filters));
        descriptors.addAll(getFilteredDescriptorsFromDirectory(fileDirectories, filters));

        if (descriptors.stream().allMatch(TaskDescriptor::isSortable)) {
            //if all descriptors are sorted, we return a sorted collection
            return new LoadedStage(descriptors.stream().sorted().collect(Collectors.toList()), parallel);

        } else if (descriptors.parallelStream().anyMatch(TaskDescriptor::isSortable)) {
            //if at least one of them are sorted, but not all. An exception is thrown
            throw new IllegalArgumentException("Either all tasks are ordered or none is");

        } else {
            //if none of the tasks are sorted, an unsorted collection is returned
            return new LoadedStage(descriptors, parallel);
        }
    }

    private static Collection<TaskDescriptor> getFilteredDescriptorsFromCodePackages(Collection<String> codePackages, Collection<TaskFilter> filters) {

        return codePackages.
                stream()
                .map(ReflectionUtil::loadClassesFromPackage)
                .flatMap(Collection::stream)
                .filter(source -> filters.stream().allMatch(filter -> filter.filter(source)))
                .map(ReflectionTaskDescriptorBuilder.recycledBuilder()::setSource)
                .map(ReflectionTaskDescriptorBuilder::build)
                .collect(Collectors.toList());
    }

    //TODO implement this
    private static Collection<TaskDescriptor> getFilteredDescriptorsFromDirectory(Collection<String> directories, Collection<TaskFilter> filters) {

        return directories.stream()
                .map(FileUtil::loadFilesFromDirectory)
                .flatMap(Collection::stream)
                .map(file -> FileUtil.getFromYamlFile(file, TemplatedTaskDefinition.class))
//                .filter(source -> filters.stream().allMatch(filter -> filter.filter(source)))
                .map(TemplatedTaskDescriptorBuilder.recycledBuilder()::setFromDefinition)
                .map(TemplatedTaskDescriptorBuilder::build)
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
