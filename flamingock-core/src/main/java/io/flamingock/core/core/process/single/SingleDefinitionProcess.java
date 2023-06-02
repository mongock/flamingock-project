package io.flamingock.core.core.process.single;

import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.process.DefinitionProcess;
import io.flamingock.core.core.process.LoadedProcess;
import io.flamingock.core.core.task.descriptor.ReflectionTaskDescriptor;
import io.flamingock.core.core.util.ReflectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SingleDefinitionProcess implements DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final List<String> scanPackages;

    public SingleDefinitionProcess(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }


    @Override
    public LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> load() {
        List<ReflectionTaskDescriptor> descriptors = scanPackages.stream()
                .map(ReflectionUtil::loadClassesFromPackage)
                .flatMap(Collection::stream)
                .map(source -> ReflectionTaskDescriptor.builder().setSource(source))
                .map(ReflectionTaskDescriptor.Builder::build)
                .collect(Collectors.toList());
        return new SingleLoadedProcess(descriptors);
    }
}
