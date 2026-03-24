// IronLedger.java
// Entry point for the application.
// Creates all the managers, loads saved data, and runs the menu.
// All actual logic lives in InventoryManager, SalesManager and FileHandler.

import java.util.List;
import java.util.Scanner;

public class IronLedger {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        FileHandler fileHandler = new FileHandler();

        // load saved inventory from csv first
        List<Item> items = fileHandler.loadInventory();

        // wire the managers together
        InventoryManager inventoryManager = new InventoryManager(items, scanner);
        SalesManager salesManager = new SalesManager(inventoryManager, scanner);

        int choice;

        do {
            System.out.println("===== IronLedger Menu =====");
            System.out.println("1. Add new item(s) to inventory");
            System.out.println("2. Enter sales data");
            System.out.println("3. Display reorder list");
            System.out.println("4. Restock existing item");
            System.out.println("5. View all inventory");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = inventoryManager.getIntInput();

            switch (choice) {
                case 1:
                    inventoryManager.enterStockData();
                    break;
                case 2:
                    salesManager.enterSalesData();
                    break;
                case 3:
                    inventoryManager.displayReorderList();
                    break;
                case 4:
                    inventoryManager.restockItem();
                    break;
                case 5:
                    inventoryManager.viewAllInventory();
                    break;
                case 6:
                    fileHandler.saveInventory(inventoryManager.getItems());
                    fileHandler.saveSales(salesManager.getSalesHistory());
                    System.out.println("Exiting IronLedger...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-6.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
