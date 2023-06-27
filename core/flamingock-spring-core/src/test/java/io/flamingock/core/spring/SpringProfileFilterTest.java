package io.flamingock.core.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.AnnotatedElement;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringProfileFilterTest {

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[] IF appliedProfiles=[]")
    void trueIfNotProfilesApplied() {
        // Given
        AnnotatedElement taskClass = mock(AnnotatedElement.class);
        when(taskClass.isAnnotationPresent(Profile.class)).thenReturn(false);

        // When-Then
        assertTrue(new SpringProfileFilter().filter(taskClass));
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    void isBlank_ShouldReturnTrueForNullOrBlankStrings(
            String[] activeProfiles,
            String[] profileStrings,
            boolean expected) {
        // Given
        AnnotatedElement taskClass = mock(AnnotatedElement.class);
        when(taskClass.isAnnotationPresent(Profile.class)).thenReturn(true);
        Profile taskProfile = mock(Profile.class);
        when(taskProfile.value()).thenReturn(profileStrings);
        when(taskClass.getAnnotation(Profile.class)).thenReturn(taskProfile);

        // When-Then
        assertEquals(expected, new SpringProfileFilter(activeProfiles).filter(taskClass));
    }


    private static Stream<Arguments> provideTestArguments() {
        return Stream.of(
                //true
                Arguments.of(new String[]{}, new String[]{}, true),
                Arguments.of(new String[]{"P1"}, new String[]{"P1"}, true),
                Arguments.of(new String[]{"P1","P2"}, new String[]{"P1"}, true),
                Arguments.of(new String[]{"P1"}, new String[]{"P1", "P2"}, true),
                Arguments.of(new String[]{"P2"}, new String[]{"!P1"}, true),
                //false
                Arguments.of(new String[]{}, new String[]{"P1"}, false),
                Arguments.of(new String[]{"P2"}, new String[]{"P1"}, false),
                Arguments.of(new String[]{"P1"}, new String[]{"!P1"}, false),
                Arguments.of(new String[]{"P1","P2"}, new String[]{"!P1"}, false)
        );
    }
}