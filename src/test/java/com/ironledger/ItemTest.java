package com.ironledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void isBelowThreshold_returnsTrue_whenStockIsLowerThanThreshold() {
        Item item = new Item("ML1", "Milk", 3, 1.59, 1.99, 5, 10);   // arrange
        assertTrue(item.isBelowThreshold());                         // act + assert
    }

    @Test
    void isBelowThreshold_returnsFalse_whenStockIsAboveThreshold() {
        Item item = new Item("ML1", "Milk", 17, 1.59, 1.99, 5, 10);
        assertFalse(item.isBelowThreshold());
    }

    @Test
    void isBelowThreshold_returnsFalse_whenStockEqualsThreshold() {
        Item item = new Item("ML1", "Milk", 5, 1.59, 1.99, 5, 10);
        assertFalse(item.isBelowThreshold());
    }

    @Test
    void setStockLevel_changesTheStock() {
        Item item = new Item("ML1", "Milk", 17, 1.59, 1.99, 5, 10);
        item.setStockLevel(12);
        assertEquals(12, item.getStockLevel());
    }
}