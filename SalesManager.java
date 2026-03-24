// SalesManager.java
// Handles recording sales and tracking sale history for the session.
// Talks to InventoryManager to find items and update stock levels.
// FileHandler saves the sales list on exit.

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalesManager {

    private List<Sale> salesHistory;
    private InventoryManager inventoryManager;

    public SalesManager(InventoryManager inventoryManager, Scanner scanner) {
        this.inventoryManager = inventoryManager;
        this.salesHistory = new ArrayList<>();
    }

    // returns the list so FileHandler can save it on exit
    public List<Sale> getSalesHistory() {
        return salesHistory;
    }

    public void enterSalesData() {
        if (inventoryManager.getItems().isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        double totalSales = 0;
        int saleCount = 0;

        do {
            saleCount++;
            System.out.println("\nRecording sale " + saleCount);

            System.out.print("Enter Item ID: ");
            String saleId = inventoryManager.scanner.nextLine().trim();

            Item item = inventoryManager.findItemById(saleId);
            if (item == null) {
                System.out.println("Item not found. Try again.");
                saleCount--;
                continue;
            }

            System.out.print("Enter quantity sold: ");
            int qty = inventoryManager.getIntInput();

            if (qty < 0) {
                System.out.println("Quantity cannot be negative. Try again.");
                saleCount--;
                continue;
            }

            if (qty > item.getStockLevel()) {
                System.out.println("Not enough stock. Available: " + item.getStockLevel());
                saleCount--;
                continue;
            }

            item.setStockLevel(item.getStockLevel() - qty);
            double saleAmount = qty * item.getUnitPrice();
            totalSales += saleAmount;

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            salesHistory.add(new Sale(item.getId(), item.getName(), qty, saleAmount, timestamp));

            System.out.printf("✅ Sale recorded! Amount: %.2f%n", saleAmount);

            if (item.isBelowThreshold()) {
                System.out.println("⚠️  " + item.getName() + " is below threshold! Consider reordering " + item.getReorderQty() + " units.");
            }

        } while (inventoryManager.askYesNo("\nRecord another sale? (y/n): "));

        System.out.printf("\nTotal sales amount: %.2f\n\n", totalSales);
    }
}