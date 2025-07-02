package io.flamingock.internal.core.task.loaded;

import io.flamingock.internal.common.core.error.FlamingockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadedChangeUnitUtilTest {

    @Test
    @DisplayName("Should return orderInContent when orderInContent is present and no order in fileName")
    void shouldReturnOrderInContentWhenOrderInContentPresentAndNoOrderInFileName() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "001";
        String fileName = "test-file.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("001", result);
    }

    @Test
    @DisplayName("Should return orderInContent when orderInContent matches order in fileName")
    void shouldReturnOrderInContentWhenOrderInContentMatchesOrderInFileName() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "001";
        String fileName = "test-file_001_.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("001", result);
    }

    @Test
    @DisplayName("Should throw exception when orderInContent does not match order in fileName")
    void shouldThrowExceptionWhenOrderInContentDoesNotMatchOrderInFileName() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "001";
        String fileName = "test-file_002_.yml";

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () ->
            LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName)
        );

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order mismatch"));
        assertTrue(exception.getMessage().contains("orderInContent='001'"));
        assertTrue(exception.getMessage().contains("order in fileName='002'"));
    }

    @Test
    @DisplayName("Should return order from fileName when orderInContent is null and order in fileName is present")
    void shouldReturnOrderFromFileNameWhenOrderInContentIsNullAndOrderInFileNameIsPresent() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = null;
        String fileName = "test-file_003_.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("003", result);
    }

    @Test
    @DisplayName("Should return order from fileName when orderInContent is empty and order in fileName is present")
    void shouldReturnOrderFromFileNameWhenOrderInContentIsEmptyAndOrderInFileNameIsPresent() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "";
        String fileName = "test-file_004_.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("004", result);
    }

    @Test
    @DisplayName("Should return order from fileName when orderInContent is blank and order in fileName is present")
    void shouldReturnOrderFromFileNameWhenOrderInContentIsBlankAndOrderInFileNameIsPresent() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "   ";
        String fileName = "test-file_005_.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("005", result);
    }

    @Test
    @DisplayName("Should throw exception when both orderInContent and order in fileName are missing")
    void shouldThrowExceptionWhenBothOrderInContentAndOrderInFileNameAreMissing() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = null;
        String fileName = "test-file.yml";

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () ->
            LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName)
        );

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order is required"));
        assertTrue(exception.getMessage().contains("neither orderInContent nor order in fileName is provided"));
    }

    @Test
    @DisplayName("Should throw exception when orderInContent is empty and no order in fileName")
    void shouldThrowExceptionWhenOrderInContentIsEmptyAndNoOrderInFileName() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "";
        String fileName = "test-file.yml";

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () ->
            LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName)
        );

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order is required"));
    }

    @Test
    @DisplayName("Should extract order from fileName with various formats")
    void shouldExtractOrderFromFileNameWithVariousFormats() {
        // Test different file name formats
        String changeUnitId = "test-id";
        
        // With extension
        String result1 = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, null, "migration_001_.sql");
        assertEquals("001", result1);
        
        // Without extension
        String result2 = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, null, "migration_002_");
        assertEquals("002", result2);
        
        // With prefix - note: regex extracts first match between underscores
        String result3 = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, null, "prefix_migration_.yaml");
        assertEquals("migration", result3);
        
        // Non-numeric order
        String result4 = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, null, "migration_alpha_.yml");
        assertEquals("alpha", result4);
        
        // Complex order
        String result5 = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, null, "migration_v1.2.3_.yml");
        assertEquals("v1.2.3", result5);
    }

    @Test
    @DisplayName("Should handle null fileName gracefully")
    void shouldHandleNullFileNameGracefully() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "001";
        String fileName = null;

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("001", result);
    }

    @Test
    @DisplayName("Should throw exception when fileName is null and orderInContent is missing")
    void shouldThrowExceptionWhenFileNameIsNullAndOrderInContentIsMissing() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = null;
        String fileName = null;

        // When & Then
        FlamingockException exception = assertThrows(FlamingockException.class, () ->
            LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName)
        );

        assertTrue(exception.getMessage().contains("ChangeUnit[test-id] Order is required"));
    }

    @Test
    @DisplayName("Should handle multiple underscores in fileName - extract first match")
    void shouldHandleMultipleUnderscoresInFileName() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = null;
        String fileName = "migration_001__002_.yml";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("001", result); // Should extract the first match
    }

    @Test
    @DisplayName("Should handle fileName without proper underscore pattern")
    void shouldHandleFileNameWithoutProperUnderscorePattern() {
        // Given
        String changeUnitId = "test-id";
        String orderInContent = "001";
        String fileName = "migration_incomplete";

        // When
        String result = LoadedChangeUnitUtil.getOrderFromContentOrFileName(changeUnitId, orderInContent, fileName);

        // Then
        assertEquals("001", result); // Should return orderInContent since no valid pattern in fileName
    }
}