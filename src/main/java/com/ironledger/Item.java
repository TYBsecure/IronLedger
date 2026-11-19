package com.ironledger;

// This class is purely a data container - it just holds the info for one product.
// No logic, no menus, no file reading. Just the data and ways to access it.

public class Item {

    private String id;
    private String name;
    private int stockLevel;
    private double unitCost;
    private double unitPrice;
    private int threshold;
    private int reorderQty;

    public Item(String id, String name, int stockLevel, double unitCost, double unitPrice, int threshold, int reorderQty) {
        this.id = id;
        this.name = name;
        this.stockLevel = stockLevel;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.threshold = threshold;
        this.reorderQty = reorderQty;
    }

    // getters - other classes read data through these
    public String getId()        { return id; }
    public String getName()      { return name; }
    public int getStockLevel()   { return stockLevel; }
    public double getUnitCost()  { return unitCost; }
    public double getUnitPrice() { return unitPrice; }
    public int getThreshold()    { return threshold; }
    public int getReorderQty()   { return reorderQty; }

    // only stock changes during the program's life - so only this gets a setter
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }

    public boolean isBelowThreshold() {
        return stockLevel < threshold;
    }
}