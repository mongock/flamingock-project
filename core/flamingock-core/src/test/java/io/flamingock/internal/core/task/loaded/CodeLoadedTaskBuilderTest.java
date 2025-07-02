package io.flamingock.internal.core.task.loaded;

import io.flamingock.internal.common.core.error.FlamingockException;
import io.flamingock.api.annotations.ChangeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeLoadedTaskBuilderTest {

    private CodeLoadedTaskBuilder builder;

    @BeforeEach
    void setUp() {
        builder = CodeLoadedTaskBuilder.getInstance();
    }

    @Test
    @DisplayName("Should build with orderInContent when orderInContent is present and no order in source")
    void shouldBuildWithOrderInContentWhenOrderInContentPresentAndNoOrderInSource() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("001")
                .setTemplateName("java.lang.String") // Using existing class for simplicity
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When
        CodeLoadedChangeUnit result = builder.build();

        // Then
        assertEquals("001", result.getOrder().orElse(null));
        assertEquals("test-id", result.getId());
        assertEquals(String.class, result.getImplementationClass());
    }

    @Test
    @DisplayName("Should build with order from source when orderInContent is null and order in source is present")
    void shouldBuildWithOrderFromSourceWhenOrderInContentIsNullAndOrderInSourceIsPresent() {
        // Given
        builder.setId("test-id")
                .setOrderInContent(null)
                .setTemplateName("package.MyClass_002_")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        // This will throw ClassNotFoundException since the class doesn't exist
        // But it will call the order validation before that, so we can test the order logic
        RuntimeException exception = assertThrows(RuntimeException.class, () -> builder.build());
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    @DisplayName("Should build with orderInContent when orderInContent matches order in source")
    void shouldBuildWithOrderInContentWhenOrderInContentMatchesOrderInSource() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("String")
                .setTemplateName("java.lang_String_") // This will extract "String" from between underscores
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        // This will throw ClassNotFoundException, but order validation happens first
        RuntimeException exception = assertThrows(RuntimeException.class, () -> builder.build());
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    @DisplayName("Should throw exception when orderInContent does not match order in source")
    void shouldThrowExceptionWhenOrderInContentDoesNotMatchOrderInSource() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("001")
                .setTemplateName("package.MyClass_002_")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () -> builder.build());

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order mismatch"));
        assertTrue(exception.getMessage().contains("orderInContent='001'"));
        assertTrue(exception.getMessage().contains("order in fileName='002'"));
    }

    @Test
    @DisplayName("Should throw exception when both orderInContent and order in source are missing")
    void shouldThrowExceptionWhenBothOrderInContentAndOrderInSourceAreMissing() {
        // Given
        builder.setId("test-id")
                .setOrderInContent(null)
                .setTemplateName("java.lang.String")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () -> builder.build());

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order is required"));
        assertTrue(exception.getMessage().contains("neither orderInContent nor order in fileName is provided"));
    }

    @Test
    @DisplayName("Should build with order from source when orderInContent is empty string")
    void shouldBuildWithOrderFromSourceWhenOrderInContentIsEmptyString() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("")
                .setTemplateName("package.MyClass_004_")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        // This will throw ClassNotFoundException, but order validation happens first
        RuntimeException exception = assertThrows(RuntimeException.class, () -> builder.build());
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    @DisplayName("Should build with order from source when orderInContent is blank string")
    void shouldBuildWithOrderFromSourceWhenOrderInContentIsBlankString() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("   ")
                .setTemplateName("package.MyClass_005_")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When & Then
        // This will throw ClassNotFoundException, but order validation happens first
        RuntimeException exception = assertThrows(RuntimeException.class, () -> builder.build());
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    @DisplayName("Should work with real class when order validation passes")
    void shouldWorkWithRealClassWhenOrderValidationPasses() {
        // Given - using a real class that exists
        builder.setId("test-id")
                .setOrderInContent("001")
                .setTemplateName("java.lang.String")
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When
        CodeLoadedChangeUnit result = builder.build();

        // Then
        assertEquals("001", result.getOrder().orElse(null));
        assertEquals("test-id", result.getId());
        assertEquals(String.class, result.getImplementationClass());
        assertFalse(result.isRunAlways());
        assertTrue(result.isTransactional());
        assertFalse(result.isSystem());
    }

    @Test
    @DisplayName("Should handle beforeExecution flag correctly")
    void shouldHandleBeforeExecutionFlagCorrectly() {
        // Given
        builder.setId("test-id")
                .setOrderInContent("001")
                .setTemplateName("java.lang.String")
                .setBeforeExecution(true)
                .setRunAlways(false)
                .setTransactional(true)
                .setSystem(false);

        // When
        CodeLoadedChangeUnit result = builder.build();

        // Then
        assertEquals("test-id_before", result.getId()); // Should append "_before" when beforeExecution is true
        assertEquals("001", result.getOrder().orElse(null));
        assertEquals(String.class, result.getImplementationClass());
    }

    // Test class with ChangeUnit annotation for testing setFromFlamingockChangeAnnotation
    @ChangeUnit(id = "annotation-test", order = "100", runAlways = true, transactional = false)
    static class TestChangeUnitClass {
    }

    @Test
    @DisplayName("Should build from annotated class correctly")
    void shouldBuildFromAnnotatedClassCorrectly() {
        // Given
        CodeLoadedTaskBuilder builderFromClass = CodeLoadedTaskBuilder.getInstanceFromClass(TestChangeUnitClass.class);

        // When
        CodeLoadedChangeUnit result = builderFromClass.build();

        // Then
        assertEquals("annotation-test", result.getId());
        assertEquals("100", result.getOrder().orElse(null));
        assertEquals(TestChangeUnitClass.class, result.getImplementationClass());
        assertTrue(result.isRunAlways());
        assertFalse(result.isTransactional());
        assertFalse(result.isSystem());
    }

    @Test
    @DisplayName("Should support annotated class check")
    void shouldSupportAnnotatedClassCheck() {
        // When & Then
        assertTrue(CodeLoadedTaskBuilder.supportsSourceClass(TestChangeUnitClass.class));
        assertFalse(CodeLoadedTaskBuilder.supportsSourceClass(String.class));
    }
}