// Sale.java
// Represents a single sale transaction.

public class Sale {

    private String itemId;
    private String itemName;
    private int quantity;
    private double totalAmount;
    private String timestamp;

    public Sale(String itemId, String itemName, int quantity, double totalAmount, String timestamp) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }

    public String getItemId()     { return itemId; }
    public String getItemName()   { return itemName; }
    public int getQuantity()      { return quantity; }
    public double getTotalAmount(){ return totalAmount; }
    public String getTimestamp()  { return timestamp; }

    // formats this sale as a csv line for saving to file
    public String toCsvLine() {
        return itemId + "," + itemName + "," + quantity + "," + totalAmount + "," + timestamp;
    }
}