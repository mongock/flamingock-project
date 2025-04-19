package io.flamingock.core.api.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A convenient base class for implementing {@link ChangeTemplate} with a predefined configuration lifecycle.
 * <p>
 * This abstract class provides a default implementation for {@link #setConfiguration(CONFIG)} and
 * {@link #getReflectiveClasses()}, and stores the configuration as an internal field.
 *
 * <p><strong>Usage:</strong> Most implementations of {@link ChangeTemplate} can extend this class directly,
 * unless they require full control over configuration injection or lifecycle management.
 *
 * @param <CONFIG> The type of configuration used by the template.
 */
public abstract class AbstractChangeTemplate<CONFIG> implements ChangeTemplate<CONFIG> {
    private final Logger logger = LoggerFactory.getLogger("AbstractChangeTemplate");

    private final Set<Class<?>> reflectiveClasses;

    /**
     * Constructs the template with the specified additional reflective classes.
     * <p>
     * This constructor allows registering extra classes that require reflective access,
     * in addition to the configuration class ({@code CONFIG}), which is always included by default.
     * This is especially useful in environments like GraalVM Native Image where all reflectively accessed
     * types must be declared ahead of time.
     *
     * <p><strong>Important:</strong> The set of reflective classes is fixed at construction time
     * and cannot be modified later. If your implementation needs to expose multiple types for reflection,
     * provide them all when instantiating the template.
     *
     * @param additionalReflectiveClass Optional vararg array of additional classes to include for reflection.
     *                                  Must not be {@code null}. Can be empty.
     * @throws IllegalArgumentException if {@code additionalReflectiveClass} is {@code null}.
     */
    public AbstractChangeTemplate(Class<?>... additionalReflectiveClass) {
        if (additionalReflectiveClass == null) {
            throw new IllegalArgumentException("additionalReflectiveClass must not be null");
        }
        reflectiveClasses = new HashSet<>(Arrays.asList(additionalReflectiveClass));
        reflectiveClasses.add(getConfigClass());

    }



    /**
     * The configuration instance provided via {@link #setConfiguration(CONFIG)}.
     * <p>
     */
    protected CONFIG configuration;

    /**
     * Returns a collection of classes that should be included for reflective access.
     * <p>
     * By default, this includes the configuration class (as resolved from the generic parameter {@code CONFIG})
     * and any additional classes explicitly passed to the constructor. This is particularly useful for environments
     * like GraalVM Native Image where reflective access must be declared ahead of time.
     *
     * <p><strong>Note:</strong> This collection is computed once during instantiation and cached internally.
     * Subclasses should call the appropriate constructor to ensure all relevant classes are included.
     *
     * @return A collection of classes required for reflection-based access.
     */
    @Override
    public final Collection<Class<?>> getReflectiveClasses() {
        return reflectiveClasses;
    }

    /**
     * Sets the configuration for this template. This is stored internally and can be used
     * by subclasses during execution.
     *
     * @param configuration The configuration instance to inject.
     */
    @Override
    public final void setConfiguration(CONFIG configuration) {
        logger.trace("setting {} config[{}]: {}", getClass(), getConfigClass(), configuration);
        this.configuration = configuration;
    }
}
