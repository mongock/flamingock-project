package io.flamingock.core.processor.util;

import io.flamingock.core.api.annotations.Change;
import io.flamingock.core.preview.AbstractPreviewTask;
import io.flamingock.core.preview.CodePreviewChangeUnit;
import io.flamingock.core.preview.builder.PreviewTaskBuilder;
import io.mongock.api.annotations.ChangeUnit;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AnnotationFinder {

    private final LoggerPreProcessor logger;
    private final RoundEnvironment roundEnv;

    public AnnotationFinder(RoundEnvironment roundEnv, LoggerPreProcessor logger) {
        this.roundEnv = roundEnv;
        this.logger = logger;
    }

    public Map<String, List<AbstractPreviewTask>> getCodedChangeUnitsMapByPackage() {
        logger.info("Searching for code-based changes (Java classes annotated with @Change or legacy @ChangeUnit annotations)");
        Collection<CodePreviewChangeUnit> allChanges = new LinkedList<>(findAnnotatedChanges(ChangeUnit.class));
        allChanges.addAll(findAnnotatedChanges(Change.class));
        Map<String, List<AbstractPreviewTask>> mapByPackage = new HashMap<>();
        for(CodePreviewChangeUnit item: allChanges) {
            mapByPackage.compute(item.getSourcePackage(), (key, descriptors) -> {
                List<AbstractPreviewTask> newDescriptors;
                if(descriptors != null) {
                    newDescriptors = descriptors;
                } else {
                    newDescriptors = new ArrayList<>();
                }
                newDescriptors.add(item);
                return newDescriptors;
            });
        }
        return mapByPackage;
    }

    private Collection<CodePreviewChangeUnit> findAnnotatedChanges(Class<? extends Annotation> annotationType) {
        return roundEnv.getElementsAnnotatedWith(annotationType)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(e -> (TypeElement) e)
                .map(PreviewTaskBuilder::getCodeBuilder)
                .map(PreviewTaskBuilder::build)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



}
