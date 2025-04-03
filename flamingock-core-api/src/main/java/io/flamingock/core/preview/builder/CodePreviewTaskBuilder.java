/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.preview.builder;

import io.flamingock.core.api.annotations.Change;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.RollbackExecution;
import io.flamingock.core.preview.CodePreviewChangeUnit;
import io.flamingock.core.preview.CodePreviewLegacyChangeUnit;
import io.flamingock.core.preview.PreviewMethod;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackBeforeExecution;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//TODO how to set transactional and runAlways
public class CodePreviewTaskBuilder implements PreviewTaskBuilder<CodePreviewChangeUnit> {

    private TypeElement typeElement;

    private String id;
    private String order;
    private String sourceClassPath;
    private PreviewMethod executionMethod;
    private PreviewMethod rollbackMethod;
    private PreviewMethod beforeExecutionMethod;
    private PreviewMethod rollbackBeforeExecutionMethod;
    private boolean runAlways;
    private boolean transactional;
    private boolean isNewChangeUnit;
    private boolean system;

    private CodePreviewTaskBuilder() {
    }

    static CodePreviewTaskBuilder builder() {
        return new CodePreviewTaskBuilder();
    }

    static CodePreviewTaskBuilder builder(TypeElement typeElement) {
        return  builder().setTypeElement(typeElement);
    }

    public CodePreviewTaskBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public CodePreviewTaskBuilder setOrder(String order) {
        this.order = order;
        return this;
    }

    public CodePreviewTaskBuilder setSourceClassPath(String sourceClassPath) {
        this.sourceClassPath = sourceClassPath;
        return this;
    }

    public CodePreviewTaskBuilder setExecutionMethod(PreviewMethod executionMethod) {
        this.executionMethod = executionMethod;
        return this;
    }

    public CodePreviewTaskBuilder setRollbackMethod(PreviewMethod rollbackMethod) {
        this.rollbackMethod = rollbackMethod;
        return this;
    }

    public void setBeforeExecutionMethod(PreviewMethod beforeExecutionMethod) {
        this.beforeExecutionMethod = beforeExecutionMethod;
    }

    public void setRollbackBeforeExecutionMethod(PreviewMethod rollbackBeforeExecutionMethod) {
        this.rollbackBeforeExecutionMethod = rollbackBeforeExecutionMethod;
    }

    public CodePreviewTaskBuilder setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
        return this;
    }

    public CodePreviewTaskBuilder setTransactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    public CodePreviewTaskBuilder setNewChangeUnit(boolean newChangeUnit) {
        isNewChangeUnit = newChangeUnit;
        return this;
    }

    public CodePreviewTaskBuilder setSystem(boolean system) {
        this.system = system;
        return this;
    }

    CodePreviewTaskBuilder setTypeElement(TypeElement typeElement) {
        Change changeAnnotation = typeElement.getAnnotation(Change.class);
        if (changeAnnotation != null) {
            setFieldsFromChange(typeElement, changeAnnotation);
        }

        ChangeUnit legacyChangeUnitAnnotation = typeElement.getAnnotation(ChangeUnit.class);
        if (legacyChangeUnitAnnotation != null) {

            setFieldsFromLegacy(typeElement, legacyChangeUnitAnnotation);
        }
        return this;
    }


    @Override
    public CodePreviewChangeUnit build() {
        return isNewChangeUnit ? getCodePreviewChange() : getCodePreviewLegacyChange();
    }

    private CodePreviewChangeUnit setFieldsFromChange(TypeElement typeElement, Change annotation) {
        setId(annotation.id());
        setOrder(annotation.order());
        setSourceClassPath(typeElement.getQualifiedName().toString());
        setExecutionMethod(getAnnotatedMethodInfo(typeElement, Execution.class).orElse(null));
        setRollbackMethod(getAnnotatedMethodInfo(typeElement, RollbackExecution.class).orElse(null));
        setTransactional(annotation.transactional());
        setRunAlways(annotation.runAlways());
        setNewChangeUnit(true);
        setSystem(false);
        return getCodePreviewChange();
    }


    private void setFieldsFromLegacy(TypeElement typeElement, ChangeUnit annotation) {
        setId(annotation.id());
        setOrder(annotation.order());
        setSourceClassPath(typeElement.getQualifiedName().toString());
        setExecutionMethod(getAnnotatedMethodInfo(typeElement, Execution.class).orElse(null));
        setRollbackMethod(getAnnotatedMethodInfo(typeElement, RollbackExecution.class).orElse(null));
        setBeforeExecutionMethod(getAnnotatedMethodInfo(typeElement, BeforeExecution.class).orElse(null));
        setRollbackBeforeExecutionMethod(getAnnotatedMethodInfo(typeElement, RollbackBeforeExecution.class).orElse(null));
        setTransactional(annotation.transactional());
        setRunAlways(annotation.runAlways());
        setNewChangeUnit(false);
        setSystem(false);
    }

    @NotNull
    private CodePreviewChangeUnit getCodePreviewChange() {
        return new CodePreviewChangeUnit(
                id,
                order,
                sourceClassPath,
                executionMethod,
                rollbackMethod,
                runAlways,
                transactional,
                isNewChangeUnit,
                system);
    }

    @NotNull
    private CodePreviewLegacyChangeUnit getCodePreviewLegacyChange() {
        return new CodePreviewLegacyChangeUnit(
                id,
                order,
                sourceClassPath,
                executionMethod,
                rollbackMethod,
                beforeExecutionMethod,
                rollbackBeforeExecutionMethod,
                runAlways,
                transactional,
                system);
    }

    private Optional<PreviewMethod> getAnnotatedMethodInfo(TypeElement typeElement,
                                                           Class<? extends Annotation> annotationType) {
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD &&
                    enclosedElement.getAnnotation(annotationType) != null) {

                ExecutableElement method = (ExecutableElement) enclosedElement;
                String methodName = method.getSimpleName().toString();

                List<String> parameterTypes = new ArrayList<>();
                for (VariableElement param : method.getParameters()) {
                    TypeMirror paramType = param.asType();
                    parameterTypes.add(paramType.toString()); // fully qualified name (e.g., java.lang.String)
                }

                return Optional.of(new PreviewMethod(methodName, parameterTypes));
            }
        }

        return Optional.empty();
    }

}
