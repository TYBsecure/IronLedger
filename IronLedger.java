import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// this class holds all the info for one product
class Item {
    String id;
    String name;
    int stockLevel;
    double unitCost;
    double unitPrice;
    int threshold;
    int reorderQty;

    // constructor to set everything up when a new item is created
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

public class IronLedger {

    // list to store all items
    static List<Item> items = new ArrayList<>();

    // keeping track of all sales so i can write them to a file later
    static List<String> salesHistory = new ArrayList<>();

    static Scanner scanner = new Scanner(System.in);

    // file names - easier to change here than hunting through all the code
    static final String INVENTORY_FILE = "inventory.csv";
    static final String SALES_FILE = "sales.csv";

    public static void main(String[] args) {
        int choice;

        // load saved data first before showing anything
        loadData();

        do {
            System.out.println("===== IronLedger Menu =====");
            System.out.println("1. Add new item(s) to inventory");
            System.out.println("2. Enter sales data");
            System.out.println("3. Display reorder list");
            System.out.println("4. Restock existing item");
            System.out.println("5. View all inventory");
            System.out.println("6. Exit");
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
                    viewAllInventory();
                    break;
                case 6:
                    saveData();
                    System.out.println("Exiting IronLedger...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-6.");
            }
        } while (choice != 6);
    }

    // keeps looping until the user actually types a number
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // have to clear the buffer or the next read breaks
        return val;
    }

    // same as above but for decimals like prices
    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input! Please enter a decimal number.");
            scanner.next();
        }
        double val = scanner.nextDouble();
        scanner.nextLine();
        return val;
    }

    // helper to ask yes or no questions - returns true for y, false for n
    private static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Invalid input! Please enter y or n.");
        }
    }

    // lets the user add new products one at a time
    private static void enterStockData() {
        int itemCount = 0;

        do {
            itemCount++;
            System.out.println("\nEntering details for item " + itemCount);

            System.out.print("Enter Item ID: ");
            String id = scanner.nextLine().trim().toUpperCase();

            // make sure no duplicate IDs get in
            if (findItemById(id) != null) {
                System.out.println("Item ID '" + id + "' already exists! Please use a unique ID.");
                itemCount--;
                continue;
            }

            System.out.print("Enter Item Name: ");
            String name = scanner.nextLine().trim();

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
            System.out.println("✅ Item '" + name + "' added successfully!");

        } while (askYesNo("\nAdd another item? (y/n): "));

        System.out.println("\nStock data entry complete. " + itemCount + " item(s) added.\n");
    }

    // records a sale, reduces stock, and saves the transaction with a timestamp
    private static void enterSalesData() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        double totalSales = 0;
        int saleCount = 0;

        do {
            saleCount++;
            System.out.println("\nRecording sale " + saleCount);

            System.out.print("Enter Item ID: ");
            String saleId = scanner.nextLine().trim();

            Item item = findItemById(saleId);
            if (item == null) {
                System.out.println("Item not found. Try again.");
                saleCount--;
                continue;
            }

            System.out.print("Enter quantity sold: ");
            int qty = getIntInput();

            if (qty < 0) {
                System.out.println("Quantity cannot be negative. Try again.");
                saleCount--;
                continue;
            }

            // can't sell what we don't have
            if (qty > item.stockLevel) {
                System.out.println("Not enough stock. Available: " + item.stockLevel);
                saleCount--;
                continue;
            }

            item.stockLevel -= qty;
            double saleAmount = qty * item.unitPrice;
            totalSales += saleAmount;

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // building the csv line for this sale
            salesHistory.add(item.id + "," + item.name + "," + qty + "," + saleAmount + "," + timestamp);

            System.out.printf("✅ Sale recorded! Amount: %.2f%n", saleAmount);

            // warn if we're running low after this sale
            if (item.stockLevel < item.threshold) {
                System.out.println("⚠️  " + item.name + " is below threshold! Consider reordering " + item.reorderQty + " units.");
            }

        } while (askYesNo("\nRecord another sale? (y/n): "));

        System.out.printf("\nTotal sales amount: %.2f\n\n", totalSales);
    }

    // shows everything that needs to be reordered and how much it would cost
    private static void displayReorderList() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        System.out.println("\n===== Reorder List =====");
        System.out.printf("%-10s %-20s %-8s %-10s %-12s %-10s %-10s\n",
                "ID", "Name", "Stock", "Threshold", "ReorderQty", "UnitCost", "Subtotal");
        System.out.println("-".repeat(85));

        double totalReorderCost = 0;
        int count = 0;

        for (Item item : items) {
            if (item.stockLevel < item.threshold) {
                double subtotal = item.reorderQty * item.unitCost;
                totalReorderCost += subtotal;
                count++;
                System.out.printf("%-10s %-20s %-8d %-10d %-12d %-10.2f %-10.2f\n",
                        item.id, item.name, item.stockLevel, item.threshold, item.reorderQty, item.unitCost, subtotal);
            }
        }

        if (count == 0) {
            System.out.println("All items are sufficiently stocked. No reorders needed.");
        } else {
            System.out.println("-".repeat(85));
            System.out.printf("Items needing reorder: %d | Total estimated cost: %.2f\n\n", count, totalReorderCost);
        }
    }

    // adds more stock to an item when a delivery comes in
    private static void restockItem() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        System.out.print("Enter the Item ID to restock: ");
        String id = scanner.nextLine().trim();

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

    // prints out everything in stock in a table, flags anything low
    private static void viewAllInventory() {
        if (items.isEmpty()) {
            System.out.println("No items in inventory yet. Add some items first.\n");
            return;
        }

        System.out.println("\n===== All Inventory =====");
        System.out.printf("%-10s %-20s %-8s %-10s %-10s %-10s %-10s\n",
                "ID", "Name", "Stock", "Cost", "Price", "Threshold", "ReorderQty");
        System.out.println("-".repeat(85));

        for (Item item : items) {
            // add a warning symbol if stock is too low
            String warning = item.stockLevel < item.threshold ? " ⚠️" : "";
            System.out.printf("%-10s %-20s %-8d %-10.2f %-10.2f %-10d %-10d%s\n",
                    item.id, item.name, item.stockLevel,
                    item.unitCost, item.unitPrice,
                    item.threshold, item.reorderQty, warning);
        }

        System.out.println("-".repeat(85));
        System.out.println("Total items in inventory: " + items.size() + "\n");
    }

    // searches the list by ID and returns the item, or null if not found
    private static Item findItemById(String id) {
        for (Item item : items) {
            if (item.id.equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    // writes inventory and sales to csv files when the user exits
    private static void saveData() {

        // save the inventory
        try (PrintWriter pw = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            for (Item item : items) {
                pw.println(item.id + "," + item.name + "," + item.stockLevel + "," +
                        item.unitCost + "," + item.unitPrice + "," +
                        item.threshold + "," + item.reorderQty);
            }
            System.out.println("✅ Inventory saved to " + INVENTORY_FILE);
        } catch (IOException e) {
            System.out.println("❌ Error saving inventory: " + e.getMessage());
        }

        // append to sales file so old data doesn't get deleted
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE, true))) {
            for (String sale : salesHistory) {
                pw.println(sale);
            }
            if (!salesHistory.isEmpty()) {
                System.out.println("✅ Sales saved to " + SALES_FILE);
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving sales: " + e.getMessage());
        }
    }

    // loads the inventory from the csv file when the app starts up
    private static void loadData() {
        File file = new File(INVENTORY_FILE);

        // if there's no file yet just skip this, nothing to load
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                // skip any lines that don't have all 7 fields
                if (parts.length == 7) {
                    String id      = parts[0];
                    String name    = parts[1];
                    int stock      = Integer.parseInt(parts[2]);
                    double cost    = Double.parseDouble(parts[3]);
                    double price   = Double.parseDouble(parts[4]);
                    int threshold  = Integer.parseInt(parts[5]);
                    int reorderQty = Integer.parseInt(parts[6]);

                    items.add(new Item(id, name, stock, cost, price, threshold, reorderQty));
                }
            }
            System.out.println("✅ Loaded " + items.size() + " item(s) from saved inventory.\n");
        } catch (IOException e) {
            System.out.println("❌ Error loading inventory: " + e.getMessage());
        }
    }
}