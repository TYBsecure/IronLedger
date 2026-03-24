// Handles all reading and writing to CSV files.


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String INVENTORY_FILE = "inventory.csv";
    private static final String SALES_FILE = "sales.csv";

    // reads the csv and returns a list of items - IronLedger.java used to do this
    public List<Item> loadInventory() {
        List<Item> items = new ArrayList<>();
        File file = new File(INVENTORY_FILE);

        if (!file.exists()) {
            return items; // nothing to load, just return empty list
        }

        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
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
            System.out.println(" Loaded " + items.size() + " item(s) from saved inventory.\n");
        } catch (IOException e) {
            System.out.println(" Error loading inventory: " + e.getMessage());
        }

        return items;
    }

    // saves current inventory list to csv - overwrites each time
    public void saveInventory(List<Item> items) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            for (Item item : items) {
                pw.println(item.getId() + "," + item.getName() + "," + item.getStockLevel() + "," +
                        item.getUnitCost() + "," + item.getUnitPrice() + "," +
                        item.getThreshold() + "," + item.getReorderQty());
            }
            System.out.println(" Inventory saved to " + INVENTORY_FILE);
        } catch (IOException e) {
            System.out.println(" Error saving inventory: " + e.getMessage());
        }
    }

    // appends new sales to the sales file - we never overwrite old sales
    public void saveSales(List<Sale> sales) {
        if (sales.isEmpty()) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE, true))) {
            for (Sale sale : sales) {
                pw.println(sale.toCsvLine());
            }
            System.out.println(" Sales saved to " + SALES_FILE);
        } catch (IOException e) {
            System.out.println(" Error saving sales: " + e.getMessage());
        }
    }
}