import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Class representing a single Item
class Item {
    String id;
    String name;
    int stockLevel;
    double unitCost;
    double unitPrice;
    int threshold;
    int reorderQty;

    public Item(String id, String name, int stockLevel, double unitCost, double unitPrice, int threshold, int reorderQty) {
        this.id = id;
        this.name = name;
        this.stockLevel = stockLevel;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.threshold = threshold;
        this.reorderQty = reorderQty;
    }
}

public class IrongLedger {
public class IronLedger {

    static List<Item> items = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("===== IrongLedger Menu =====");
            System.out.println("1. Enter stock data (new items)");
            System.out.println("2. Enter sales data");
            System.out.println("3. Display reorder list");
            System.out.println("4. Restock existing item");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = getIntInput();

            switch (choice) {
                case 1:
                    enterStockData();
                    break;
                case 2:
                    enterSalesData();
                    break;
                case 3:
                    displayReorderList();
                    break;
                case 4:
                    restockItem();
                    break;
                case 5:
                    System.out.println("Exiting IrongLedger...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-5.");
            }
        } while (choice != 5);
    }

    // Input helpers
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input! Please enter a decimal number.");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    // Add new items
    private static void enterStockData() {
        System.out.print("How many items do you want to add? ");
        int numItems = getIntInput();

        for (int i = 0; i < numItems; i++) {
            System.out.println("\nEntering details for item " + (i + 1));

            System.out.print("Enter Item ID: ");
            String id = scanner.next();

            System.out.print("Enter Item Name: ");
            String name = scanner.next();

            System.out.print("Enter Stock Level: ");
            int stock = getIntInput();

            System.out.print("Enter Unit Cost: ");
            double cost = getDoubleInput();

            System.out.print("Enter Unit Price: ");
            double price = getDoubleInput();

            System.out.print("Enter Threshold: ");
            int threshold = getIntInput();

            System.out.print("Enter Reorder Quantity: ");
            int reorder = getIntInput();

            items.add(new Item(id, name, stock, cost, price, threshold, reorder));
        }

        System.out.println("\nStock data entry complete.\n");
    }

    // Enter sales data
    private static void enterSalesData() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please enter stock data first.\n");
            return;
        }

        System.out.print("How many sales entries do you want to record? ");
        int numSales = getIntInput();

        double totalSales = 0;

        for (int i = 0; i < numSales; i++) {
            System.out.println("\nRecording sale " + (i + 1));

            System.out.print("Enter Item ID: ");
            String saleId = scanner.next();

            Item item = findItemById(saleId);
            if (item == null) {
                System.out.println("Item not found. Try again.");
                i--;
                continue;
            }

            System.out.print("Enter quantity sold: ");
            int qty = getIntInput();

            if (qty > item.stockLevel) {
                System.out.println("Not enough stock. Available: " + item.stockLevel);
                i--;
                continue;
            }

            item.stockLevel -= qty;
            double saleAmount = qty * item.unitPrice;
            totalSales += saleAmount;

            if (item.stockLevel < item.threshold) {
                System.out.println("⚠️ Item " + item.name + " is below threshold! Reorder " + item.reorderQty);
            }
        }

        System.out.printf("\nTotal sales amount: %.2f\n\n", totalSales);
    }

    // Display reorder list
    private static void displayReorderList() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please enter stock data first.\n");
            return;
        }

        System.out.println("===== Reorder List =====");
        System.out.printf("%-10s %-15s %-10s %-10s %-12s %-10s %-10s\n",
                "ID", "Name", "Stock", "Threshold", "ReorderQty", "UnitCost", "Subtotal");

        double totalReorderCost = 0;
        for (Item item : items) {
            if (item.stockLevel < item.threshold) {
                double subtotal = item.reorderQty * item.unitCost;
                totalReorderCost += subtotal;
                System.out.printf("%-10s %-15s %-10d %-10d %-12d %-10.2f %-10.2f\n",
                        item.id, item.name, item.stockLevel, item.threshold, item.reorderQty, item.unitCost, subtotal);
            }
        }

        System.out.printf("Total cost of reorders: %.2f\n\n", totalReorderCost);
    }

    // Restock an existing item
    private static void restockItem() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please enter stock data first.\n");
            return;
        }

        System.out.print("Enter the Item ID to restock: ");
        String id = scanner.next();

        Item item = findItemById(id);
        if (item == null) {
            System.out.println("Item not found!\n");
            return;
        }

        System.out.print("Enter quantity to add: ");
        int qty = getIntInput();

        item.stockLevel += qty;
        System.out.println("✅ " + item.name + " new stock level: " + item.stockLevel + "\n");
    }

    // Find item by ID
    private static Item findItemById(String id) {
        for (Item item : items) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }
}
