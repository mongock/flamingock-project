package io.flamingock.graalvm;

import io.flamingock.core.api.template.ChangeTemplate;
import io.flamingock.core.pipeline.LoadedStage;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.preview.PreviewPipeline;
import io.flamingock.core.preview.PreviewStage;
import io.flamingock.core.system.SystemModule;
import io.flamingock.core.task.AbstractTaskDescriptor;
import io.flamingock.core.task.TaskDescriptor;
import io.flamingock.core.task.loaded.AbstractLoadedChangeUnit;
import io.flamingock.core.task.loaded.AbstractLoadedTask;
import io.flamingock.core.task.loaded.AbstractReflectionLoadedTask;
import io.flamingock.core.task.loaded.CodeLoadedChangeUnit;
import io.flamingock.core.task.loaded.TemplateLoadedChangeUnit;
import io.flamingock.core.preview.AbstractCodePreviewTask;
import io.flamingock.core.preview.CodePreviewChangeUnit;
import io.flamingock.core.preview.TemplatePreviewChangeUnit;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.nio.charset.CoderResult;
import java.util.List;
import java.util.ServiceLoader;


public class RegistrationFeature implements Feature {

    private static final Logger logger = new Logger();

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        logger.initProcess("GraalVM classes registration");
        registerInternalClasses();
        registerTemplates();
        registerModules();
        registerUserClasses();
        logger.finishedProcess("GraalVM classes registration");
    }

    private static void registerInternalClasses() {
//        System.out.println("Flamingock: ...registering flamingock internal class");
        logger.initRegistration("internal classes");

        registerClass(TaskDescriptor.class.getName());
        registerClass(AbstractTaskDescriptor.class.getName());

        //preview
        registerClass(PreviewPipeline.class.getName());
        registerClass(PreviewStage.class.getName());
        registerClass(CodePreviewChangeUnit.class.getName());
        registerClass(TemplatePreviewChangeUnit.class.getName());

        //Loaded
        registerClass(Pipeline.class.getName());
        registerClass(LoadedStage.class.getName());
        registerClass(AbstractLoadedTask.class.getName());
        registerClass(AbstractReflectionLoadedTask.class.getName());
        registerClass(AbstractLoadedChangeUnit.class.getName());
        registerClass(CodeLoadedChangeUnit.class.getName());
        registerClass(TemplateLoadedChangeUnit.class.getName());

        //others
        registerClass(CoderResult.class.getName());

        logger.finishedRegistration("internal classes");
    }

    private static void registerUserClasses() {
        logger.initRegistration("user classes");
        List<String> classesToRegister = FileUtil.getClassesForRegistration();
        classesToRegister.forEach(RegistrationFeature::registerClass);
        logger.finishedRegistration("user classes");
    }

    private static void registerClass(String className) {
        try {
            registerClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }




    private void registerTemplates() {
        logger.initRegistration("templates");
        for (ChangeTemplate template : ServiceLoader.load(ChangeTemplate.class)) {
            registerClass(template.getClass());
        }
        logger.finishedRegistration("templates");
    }

    private void registerModules() {
        logger.initRegistration("system modules");
        for (SystemModule systemModule : ServiceLoader.load(SystemModule.class)) {
            PreviewStage previewStage = systemModule.getStage();
            previewStage.getTasks()
                    .stream()
                    .filter(task -> AbstractCodePreviewTask.class.isAssignableFrom(task.getClass()))
                    .map(task -> (AbstractCodePreviewTask) task)
                    .peek(task -> System.out.println("Flamingock: registering module task: " + task.getSource()))
                    .map(AbstractTaskDescriptor::getSource)
                    .forEach(RegistrationFeature::registerClass);
            registerClass(systemModule.getClass());
        }
        logger.finishedRegistration("system modules");
    }


    private static void registerClass(Class<?> clazz) {
        logger.initClassRegistration(clazz);
        RuntimeReflection.register(clazz);
        RuntimeReflection.register(clazz.getDeclaredConstructors());
        RuntimeReflection.register(clazz.getDeclaredMethods());
        logger.finishedClassRegistration(clazz);
    }

}
