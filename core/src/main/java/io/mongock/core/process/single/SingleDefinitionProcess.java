package io.mongock.core.process.single;

import io.mongock.core.audit.single.SingleAuditProcessStatus;
import io.mongock.core.process.DefinitionProcess;
import io.mongock.core.process.LoadedProcess;
import io.mongock.core.task.descriptor.ReflectionTaskDescriptor;
import io.mongock.core.util.ReflectionUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SingleDefinitionProcess implements DefinitionProcess<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final String scanPackage;

    public SingleDefinitionProcess(String scanPackage) {
        this.scanPackage = scanPackage;
    }


    @Override
    public LoadedProcess<SingleAuditProcessStatus, SingleExecutableProcess> load() {
        List<ReflectionTaskDescriptor> descriptors = ReflectionUtil
                .loadClassesFromPackage(scanPackage)
                .stream()
                .map(source -> ReflectionTaskDescriptor.builder().setSource(source))
                .map(ReflectionTaskDescriptor.Builder::build)
                .collect(Collectors.toList());
        return new SingleLoadedProcess(descriptors);
    }
}
