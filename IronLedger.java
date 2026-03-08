import java.util.ArrayList;   // ArrayList is a resizable list — unlike arrays, it grows as you add items
import java.util.List;        // List is the general type — ArrayList is one version of it
import java.util.Scanner;     // Scanner reads input typed by the user in the terminal
import java.io.*;             // gives access to all file reading and writing tools
import java.time.LocalDateTime;            // gets the current date and time from the system
import java.time.format.DateTimeFormatter; // controls how the date and time looks when printed

// ─── Item Class ───────────────────────────────────────────────────────────────
// Represents a single product in the inventory.
// Every item added to the shop is stored as an object of this class.
class Item {
    String id;         // unique code for the item e.g. "A01"
    String name;       // full product name e.g. "Coca Cola 330ml"
    int stockLevel;    // how many units are currently in stock
    double unitCost;   // what the shop paid per unit (buying price)
    double unitPrice;  // what the shop sells it for (selling price)
    int threshold;     // minimum stock level — if stock drops below this, a reorder warning fires
    int reorderQty;    // how many units to order when restocking

    // Constructor — runs every time a new Item object is created
    // It takes in all the details and assigns them to this item's fields
    public Item(String id, String name, int stockLevel, double unitCost, double unitPrice, int threshold, int reorderQty) {
        this.id = id;               // "this.id" refers to the field above, "id" is the value passed in
        this.name = name;
        this.stockLevel = stockLevel;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.threshold = threshold;
        this.reorderQty = reorderQty;
    }
}

// ─── Main Application Class ───────────────────────────────────────────────────
// Contains all the logic for the menu, sales, restocking, and file saving.
public class IronLedger {

    // The main list that holds all inventory items while the app is running
    // ArrayList lets us add, remove, and loop through items easily
    static List<Item> items = new ArrayList<>();

    // Holds each sale as a single line of text e.g. "A01,Coca Cola,5,60.00,2026-03-08 14:35"
    // This list is written to sales.csv when the user exits
    static List<String> salesHistory = new ArrayList<>();

    // Scanner reads everything the user types in the terminal
    static Scanner scanner = new Scanner(System.in);

    // File name constants — defined once here so if you ever rename the files
    // you only change it in one place and it updates everywhere automatically
    static final String INVENTORY_FILE = "inventory.csv";
    static final String SALES_FILE = "sales.csv";

    // ─── Main Method ──────────────────────────────────────────────────────────
    // This is the entry point — Java always starts running from here
    public static void main(String[] args) {
        int choice; // stores the menu option the user picks

        // Load any previously saved inventory before showing the menu
        // If no save file exists yet, loadData() just skips silently
        loadData();

        // do-while loop keeps the menu running until the user picks Exit (6)
        // "do" means it always runs at least once before checking the condition
        do {
            System.out.println("===== IronLedger Menu =====");
            System.out.println("1. Add new item(s) to inventory");
            System.out.println("2. Enter sales data");
            System.out.println("3. Display reorder list");
            System.out.println("4. Restock existing item");
            System.out.println("5. View all inventory");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = getIntInput(); // read the user's menu choice safely

            // switch checks which number was entered and runs the matching method
            switch (choice) {
                case 1:
                    enterStockData();
                    break; // break stops it from falling into the next case
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
                    saveData(); // save everything to CSV before the app closes
                    System.out.println("Exiting IronLedger...");
                    break;
                default:
                    // runs if the user types anything other than 1–6
                    System.out.println("Invalid choice! Please enter 1-6.");
            }
        } while (choice != 6); // keep looping until user chooses Exit
    }

    // ─── Input Helpers ────────────────────────────────────────────────────────

    // Keeps asking until the user types a valid whole number
    // Prevents the app from crashing if someone types letters instead of numbers
    private static int getIntInput() {
        while (!scanner.hasNextInt()) { // hasNextInt() checks if the next input is a number
            System.out.println("Invalid input! Please enter a number.");
            scanner.next(); // discard the bad input so the loop can ask again
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // clears the leftover Enter key press from the buffer
                            // without this, the next nextLine() would read an empty string
        return val;
    }

    // Keeps asking until the user types a valid decimal number
    // Used for prices and costs which can have cents e.g. 12.50
    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) { // hasNextDouble() checks if input is a decimal number
            System.out.println("Invalid input! Please enter a decimal number.");
            scanner.next(); // discard the bad input
        }
        double val = scanner.nextDouble();
        scanner.nextLine(); // clears the leftover Enter key press from the buffer
        return val;
    }

    // Asks "y/n" and keeps asking until a valid answer is given
    // Returns true for "y", false for "n"
    private static boolean askYesNo(String prompt) {
        while (true) { // loop forever until a valid y or n is entered
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase(); // lowercase so "Y" and "y" both work
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Invalid input! Please enter y or n."); // anything else — ask again
        }
    }

    // ─── Menu Options ─────────────────────────────────────────────────────────

    // Adds new items to the inventory one at a time
    // After each item, asks if the user wants to add another
    private static void enterStockData() {
        int itemCount = 0; // tracks how many items were added in this session

        do {
            itemCount++;
            System.out.println("\nEntering details for item " + itemCount);

            System.out.print("Enter Item ID: ");
            // trim() removes accidental spaces, toUpperCase() makes "a01" and "A01" the same
            String id = scanner.nextLine().trim().toUpperCase();

            // Check if this ID already exists — every item must have a unique ID
            if (findItemById(id) != null) {
                System.out.println("Item ID '" + id + "' already exists! Please use a unique ID.");
                itemCount--; // don't count this failed attempt
                continue;    // jump back to the top of the loop and try again
            }

            System.out.print("Enter Item Name: ");
            // nextLine() reads the full line including spaces — allows names like "Coca Cola"
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

            // Create a new Item object with all the details and add it to the list
            items.add(new Item(id, name, stock, cost, price, threshold, reorder));
            System.out.println("✅ Item '" + name + "' added successfully!");

        } while (askYesNo("\nAdd another item? (y/n): ")); // keep looping while user says y

        System.out.println("\nStock data entry complete. " + itemCount + " item(s) added.\n");
    }

    // Records sales against existing inventory items
    // Reduces stock level and logs each sale with a timestamp
    private static void enterSalesData() {
        // Can't record sales if there are no items in the inventory yet
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return; // exit the method early
        }

        double totalSales = 0;  // running total of all sales in this session
        int saleCount = 0;      // tracks how many sales were recorded

        do {
            saleCount++;
            System.out.println("\nRecording sale " + saleCount);

            System.out.print("Enter Item ID: ");
            String saleId = scanner.nextLine().trim();

            // Look up the item — if it doesn't exist, ask the user to try again
            Item item = findItemById(saleId);
            if (item == null) {
                System.out.println("Item not found. Try again.");
                saleCount--; // don't count this failed attempt
                continue;    // jump back to the top of the loop
            }

            System.out.print("Enter quantity sold: ");
            int qty = getIntInput();

            // A shop can have zero sales on a quiet day, but negative sales make no sense
            if (qty < 0) {
                System.out.println("Quantity cannot be negative. Try again.");
                saleCount--;
                continue;
            }

            // Can't sell more than what's physically in stock
            if (qty > item.stockLevel) {
                System.out.println("Not enough stock. Available: " + item.stockLevel);
                saleCount--;
                continue;
            }

            item.stockLevel -= qty;                    // reduce stock by the amount sold
            double saleAmount = qty * item.unitPrice;  // calculate total value of this sale
            totalSales += saleAmount;                  // add to the running session total

            // Format the current date and time as "2026-03-08 14:35"
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // Build one line of text for this sale and add it to salesHistory
            // Format: id,name,qty,amount,timestamp — matches how sales.csv is structured
            salesHistory.add(item.id + "," + item.name + "," + qty + "," + saleAmount + "," + timestamp);

            // %.2f formats the number to 2 decimal places e.g. 60.00
            System.out.printf("✅ Sale recorded! Amount: %.2f%n", saleAmount);

            // Warn the user if stock has now dropped below the reorder threshold
            if (item.stockLevel < item.threshold) {
                System.out.println("⚠️  " + item.name + " is below threshold! Consider reordering " + item.reorderQty + " units.");
            }

        } while (askYesNo("\nRecord another sale? (y/n): ")); // keep looping while user says y

        System.out.printf("\nTotal sales amount: %.2f\n\n", totalSales);
    }

    // Displays all items whose stock has dropped below their reorder threshold
    // Also shows the estimated cost to restock each item
    private static void displayReorderList() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        System.out.println("\n===== Reorder List =====");
        // printf with %-10s means: left-align text in a column 10 characters wide
        // This keeps all columns lined up neatly regardless of name length
        System.out.printf("%-10s %-20s %-8s %-10s %-12s %-10s %-10s\n",
                "ID", "Name", "Stock", "Threshold", "ReorderQty", "UnitCost", "Subtotal");
        System.out.println("-".repeat(85)); // print a divider line

        double totalReorderCost = 0;
        int count = 0; // tracks how many items need reordering

        // Loop through every item and only print the ones below threshold
        for (Item item : items) {
            if (item.stockLevel < item.threshold) {
                double subtotal = item.reorderQty * item.unitCost; // cost to restock this item fully
                totalReorderCost += subtotal;
                count++;
                System.out.printf("%-10s %-20s %-8d %-10d %-12d %-10.2f %-10.2f\n",
                        item.id, item.name, item.stockLevel, item.threshold, item.reorderQty, item.unitCost, subtotal);
            }
        }

        if (count == 0) {
            // No items are below threshold — stock levels are healthy
            System.out.println("All items are sufficiently stocked. No reorders needed.");
        } else {
            System.out.println("-".repeat(85));
            System.out.printf("Items needing reorder: %d | Total estimated cost: %.2f\n\n", count, totalReorderCost);
        }
    }

    // Adds stock to an existing item — used when new stock arrives at the shop
    private static void restockItem() {
        if (items.isEmpty()) {
            System.out.println("No items found! Please add stock data first.\n");
            return;
        }

        System.out.print("Enter the Item ID to restock: ");
        String id = scanner.nextLine().trim();

        // Find the item — if it doesn't exist, stop here
        Item item = findItemById(id);
        if (item == null) {
            System.out.println("Item not found!\n");
            return;
        }

        System.out.print("Enter quantity to add: ");
        int qty = getIntInput();

        item.stockLevel += qty; // add the new stock to the existing amount
        System.out.println("✅ " + item.name + " new stock level: " + item.stockLevel + "\n");
    }

    // Shows every item in the inventory in a neat table
    // Items below their reorder threshold are flagged with a warning symbol
    private static void viewAllInventory() {
        if (items.isEmpty()) {
            System.out.println("No items in inventory yet. Add some items first.\n");
            return;
        }

        System.out.println("\n===== All Inventory =====");
        // Print the column headers — each %-Xs left-aligns text in a column X characters wide
        System.out.printf("%-10s %-20s %-8s %-10s %-10s %-10s %-10s\n",
                "ID", "Name", "Stock", "Cost", "Price", "Threshold", "ReorderQty");
        System.out.println("-".repeat(85)); // print a divider line under the headers

        // Loop through every item and print one row per item
        for (Item item : items) {
            // If stock is below threshold, add a warning flag at the end of the row
            String warning = item.stockLevel < item.threshold ? " ⚠️" : "";
            System.out.printf("%-10s %-20s %-8d %-10.2f %-10.2f %-10d %-10d%s\n",
                    item.id, item.name, item.stockLevel,
                    item.unitCost, item.unitPrice,
                    item.threshold, item.reorderQty, warning);
        }

        System.out.println("-".repeat(85));
        // Show total item count at the bottom
        System.out.println("Total items in inventory: " + items.size() + "\n");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    // Loops through all items and returns the one matching the given ID
    // equalsIgnoreCase means "A01" and "a01" are treated as the same
    // Returns null if no item with that ID exists
    private static Item findItemById(String id) {
        for (Item item : items) {
            if (item.id.equalsIgnoreCase(id)) {
                return item; // found it — stop searching and return it immediately
            }
        }
        return null; // reached the end of the list without finding a match
    }

    // ─── File Saving & Loading ────────────────────────────────────────────────

    // Writes all current inventory and today's sales to CSV files on disk
    // Called automatically when the user chooses Exit
    private static void saveData() {

        // --- Save Inventory ---
        // FileWriter(INVENTORY_FILE) opens the file for writing — creates it if it doesn't exist
        // PrintWriter wraps it so we can use println() to write lines easily
        // "try with resources" — the file closes automatically when done, even if there's an error
        try (PrintWriter pw = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            for (Item item : items) {
                // Write each item as one comma-separated line
                // e.g. A01,Coca Cola,50,8.50,12.00,10,24
                pw.println(item.id + "," + item.name + "," + item.stockLevel + "," +
                        item.unitCost + "," + item.unitPrice + "," +
                        item.threshold + "," + item.reorderQty);
            }
            System.out.println("✅ Inventory saved to " + INVENTORY_FILE);
        } catch (IOException e) {
            // IOException fires if the file can't be written — e.g. no permission, disk full
            System.out.println("❌ Error saving inventory: " + e.getMessage());
        }

        // --- Save Sales History ---
        // "true" as the second argument means APPEND mode — new sales are added to the bottom
        // without this, old sales would be wiped every time you exit
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE, true))) {
            for (String sale : salesHistory) {
                pw.println(sale); // each sale is already formatted as a CSV line
            }
            if (!salesHistory.isEmpty()) {
                System.out.println("✅ Sales saved to " + SALES_FILE);
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving sales: " + e.getMessage());
        }
    }

    // Reads inventory.csv and rebuilds the items list when the app starts
    // If no file exists yet, it means the app is brand new — skip loading silently
    private static void loadData() {
        File file = new File(INVENTORY_FILE); // create a File object pointing to inventory.csv

        // exists() checks if the file is actually on disk — if not, nothing to load
        if (!file.exists()) {
            return;
        }

        // BufferedReader reads the file line by line efficiently
        // FileReader opens the actual file from disk
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;

            // readLine() reads one line at a time — returns null when the file ends
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // split the line at every comma into an array

                // Only process lines that have exactly 7 fields — skips empty or broken lines
                if (parts.length == 7) {
                    String id      = parts[0]; // first value is the ID
                    String name    = parts[1]; // second is the name
                    int stock      = Integer.parseInt(parts[2]);    // convert string "50" to int 50
                    double cost    = Double.parseDouble(parts[3]);  // convert string "8.50" to double 8.50
                    double price   = Double.parseDouble(parts[4]);
                    int threshold  = Integer.parseInt(parts[5]);
                    int reorderQty = Integer.parseInt(parts[6]);

                    // Rebuild the Item object and add it back to the list
                    items.add(new Item(id, name, stock, cost, price, threshold, reorderQty));
                }
            }
            System.out.println("✅ Loaded " + items.size() + " item(s) from saved inventory.\n");
        } catch (IOException e) {
            System.out.println("❌ Error loading inventory: " + e.getMessage());
        }
    }
}