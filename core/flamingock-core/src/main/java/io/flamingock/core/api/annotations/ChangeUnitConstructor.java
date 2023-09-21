package io.flamingock.core.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For old classes, this annotation can remain. It won't be removed from the legacy library.
 * For new classes, use FlamingockConstructor
 * <p>
 * @see FlamingockConstructor
 */
@Deprecated
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeUnitConstructor {
}
