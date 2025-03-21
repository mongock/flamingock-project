package io.flamingock.core.metadata.util;

import io.flamingock.core.api.annotations.ChangeUnitSource;
import io.flamingock.core.task.preview.CodedPreviewChangeUnit;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class AnnotationFinder {

    private final LoggerPreProcessor logger;
    private final RoundEnvironment roundEnv;

    public AnnotationFinder(RoundEnvironment roundEnv, LoggerPreProcessor logger) {
        this.roundEnv = roundEnv;
        this.logger = logger;
    }

    public Map<String, List<CodedPreviewChangeUnit>> getCodedChangeUnitsMapByPackage() {
        logger.info("Searching coded changeUnits");
        Collection<CodedPreviewChangeUnit> andProcessAnnotatedElements = findAndProcessAnnotatedElements(
                ChangeUnitSource.class,
                e -> e.getKind() == ElementKind.CLASS,
                e -> (TypeElement) e,
                this::registerChangeTemplate
        );
        return andProcessAnnotatedElements.stream().collect(Collectors.groupingBy(CodedPreviewChangeUnit::getSourcePackage));
    }

    public <I, R> Collection<R> findAndProcessAnnotatedElements(
            Class<? extends Annotation> annotation,
            Predicate<Element> filterPredicate,
            Function<Element, I> mapper,
            Function<I, R> processor) {
        return roundEnv.getElementsAnnotatedWith(annotation)
                .stream()
                .filter(filterPredicate)
                .map(mapper)
                .map(processor)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private CodedPreviewChangeUnit registerChangeTemplate(TypeElement typeElement) {
        String fullClassName = typeElement.getQualifiedName().toString();
        ChangeUnitSource annotation = typeElement.getAnnotation(ChangeUnitSource.class);
        CodedPreviewChangeUnit changeUnit =  new CodedPreviewChangeUnit(
                annotation.id(),
                annotation.order(),
                fullClassName,
                false,
                annotation.transactional(),
                true);
        logger.info("Processed changeUnit(from class): " + changeUnit.getId() + "[" + changeUnit.getSource() + "]");
        return changeUnit;
    }

}
