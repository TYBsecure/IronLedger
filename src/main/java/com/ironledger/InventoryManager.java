package com.ironledger;


// Handles everything to do with inventory - adding, viewing, restocking, and reorder checks.


import java.util.List;
import java.util.Scanner;

public class InventoryManager {

    private List<Item> items;
    Scanner scanner;

    public InventoryManager(List<Item> items, Scanner scanner) {
        this.items = items;
        this.scanner = scanner;
    }

    // returns the list so FileHandler can save it on exit
    public List<Item> getItems() {
        return items;
    }

    public void enterStockData() {
        int itemCount = 0;

        do {
            itemCount++;
            System.out.println("\nEntering details for item " + itemCount);

            System.out.print("Enter Item ID: ");
            String id = scanner.nextLine().trim().toUpperCase();

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

    public void restockItem() {
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

        item.setStockLevel(item.getStockLevel() + qty);
        System.out.println("✅ " + item.getName() + " new stock level: " + item.getStockLevel() + "\n");
    }

    public void displayReorderList() {
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
            if (item.isBelowThreshold()) {
                double subtotal = item.getReorderQty() * item.getUnitCost();
                totalReorderCost += subtotal;
                count++;
                System.out.printf("%-10s %-20s %-8d %-10d %-12d %-10.2f %-10.2f\n",
                        item.getId(), item.getName(), item.getStockLevel(),
                        item.getThreshold(), item.getReorderQty(),
                        item.getUnitCost(), subtotal);
            }
        }

        if (count == 0) {
            System.out.println("All items are sufficiently stocked. No reorders needed.");
        } else {
            System.out.println("-".repeat(85));
            System.out.printf("Items needing reorder: %d | Total estimated cost: %.2f\n\n", count, totalReorderCost);
        }
    }

    public void viewAllInventory() {
        if (items.isEmpty()) {
            System.out.println("No items in inventory yet. Add some items first.\n");
            return;
        }

        System.out.println("\n===== All Inventory =====");
        System.out.printf("%-10s %-20s %-8s %-10s %-10s %-10s %-10s\n",
                "ID", "Name", "Stock", "Cost", "Price", "Threshold", "ReorderQty");
        System.out.println("-".repeat(85));

        for (Item item : items) {
            String warning = item.isBelowThreshold() ? " ⚠️" : "";
            System.out.printf("%-10s %-20s %-8d %-10.2f %-10.2f %-10d %-10d%s\n",
                    item.getId(), item.getName(), item.getStockLevel(),
                    item.getUnitCost(), item.getUnitPrice(),
                    item.getThreshold(), item.getReorderQty(), warning);
        }

        System.out.println("-".repeat(85));
        System.out.println("Total items in inventory: " + items.size() + "\n");
    }

    // package-level access so SalesManager can look up items by ID too
    Item findItemById(String id) {
        for (Item item : items) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    // input helpers live here since both this class and SalesManager need them
    int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input! Please enter a decimal number.");
            scanner.next();
        }
        double val = scanner.nextDouble();
        scanner.nextLine();
        return val;
    }

    boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Invalid input! Please enter y or n.");
        }
    }
}