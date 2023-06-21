package io.flamingock.commuinty.runner.springboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableConfigurationProperties
@Import({CommunitySpringbootContext.class, CommunitySpringbootConfigurationProperties.class})
public @interface EnableFlamingock {
}
