package io.flamingock.graalvm;

import io.flamingock.core.pipeline.PreviewPipeline;
import io.flamingock.core.pipeline.PreviewStage;
import io.flamingock.core.task.preview.ReflectionPreviewTask;
import io.flamingock.core.task.preview.TemplatePreviewChangeUnit;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.flamingock.core.api.metadata.Constants.FULL_GRAALVM_REFLECT_CLASSES_PATH;
import static io.flamingock.core.api.metadata.Constants.TEMPLATED_GRAALVM_REFLECT_CLASSES_PATH;


public class RegistrationFeature implements Feature {
    private static final ClassLoader classLoader = RegistrationFeature.class.getClassLoader();

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        registerFlamingockClasses();
        registerUserClasses();
    }

    private static void registerFlamingockClasses() {
        registerClass(PreviewPipeline.class.getName());
        registerClass(PreviewStage.class.getName());
        registerClass(ReflectionPreviewTask.class.getName());
        registerClass(CoderResult.class.getName());
        registerClass(TemplatePreviewChangeUnit.class.getName());
    }

    private static void registerUserClasses() {
        List<String> classesToRegister= getClassesForRegistration();
        classesToRegister.forEach(RegistrationFeature::registerClass);
    }

    private static List<String> getClassesForRegistration() {
        List<String> classesToRegister = fromFile(FULL_GRAALVM_REFLECT_CLASSES_PATH);
        if(!classesToRegister.isEmpty()) {
            return classesToRegister;
        }
        classesToRegister = fromFile(TEMPLATED_GRAALVM_REFLECT_CLASSES_PATH);
        if(!classesToRegister.isEmpty()) {
            return classesToRegister;
        }
        throw new RuntimeException("Flamingock: No valid GraalVM reflection file found");
    }

    private static List<String> fromFile(String filePath) {
        ClassLoader classLoader = RegistrationFeature.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                return Collections.emptyList();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.toList());
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file `%s`", filePath), e);
        }
    }

    private static void registerClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            RuntimeReflection.register(clazz);
            RuntimeReflection.register(clazz.getDeclaredConstructors());
            RuntimeReflection.register(clazz.getDeclaredMethods());
            System.out.printf("Flamingock: Registered class[%s]%n", className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
