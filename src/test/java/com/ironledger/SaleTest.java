package com.ironledger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void toCsvLine_joinsTheFieldsWithCommas() {
        Sale sale = new Sale("ML1", "Milk", 4, 7.96, "2026-03-24 22:54");
        assertEquals("ML1,Milk,4,7.96,2026-03-24 22:54", sale.toCsvLine());
    }
}