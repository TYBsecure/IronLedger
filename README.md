# IronLedger

A simple Java inventory management system built with OOP principles.

## Features
- Add and manage stock items
- Record sales and calculate totals
- Reorder alerts when stock drops below threshold
- Restock existing items
- Data saved automatically to CSV on exit

## How to Run

**Using terminal:**
```bash
javac *.java
java IronLedger
```

**Using VS Code:**
Open `IronLedger.java` and click the Run button above the main method.

## Project Structure
- `IronLedger.java` — entry point and menu
- `InventoryManager.java` — manages stock items
- `SalesManager.java` — handles sales transactions
- `FileHandler.java` — reads and writes CSV files
- `Item.java` — product data model
- `Sale.java` — sale transaction data model

Still in the making. New upgrades coming soon.