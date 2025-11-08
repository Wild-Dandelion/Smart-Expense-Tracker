# 💰 Smart Expense Tracking System

💡 “Track your expenses today, master your finances tomorrow.”


A modern **JavaFX-based Expense Tracker** designed to help users efficiently manage, visualize, and export their daily spending.  
It offers a sleek, dark-themed interface with intuitive navigation, automatic data persistence, OCR-based input, and Excel export functionality.

---

## ✨ Preview

| Dashboard | <img width="1918" height="996" alt="dashboard" src="https://github.com/user-attachments/assets/ea3b6a8a-f678-450b-bb07-9c2d8d54aa9a" />


| Add Expense |  <img width="1918" height="948" alt="add-expense" src="https://github.com/user-attachments/assets/e98654b7-7280-48c2-9579-ee0c60e758b2" />

| Expense Table | <img width="1643" height="908" alt="image" src="https://github.com/user-attachments/assets/53173139-f9f3-49cc-b56a-011f493eaf3f" />

| Settings |  

---


## 🧠 Overview

The **Smart Expense Tracker** is a desktop application built with **JavaFX** and **Maven**, allowing users to:

- Log and categorize daily expenses  
- Automatically load saved data on startup  
- Visualize spending via a **Pie Chart**  
- Auto-fill details from receipt images using **OCR (Tess4J)**  
- Export all expenses to a **formatted Excel (.xlsx)** file  

---

## 🚀 Features

### 📊 Dashboard
- Displays **Total Spent** and **Monthly Average** dynamically.  
- Uses a **Pie Chart** to show expense distribution by category (Food, Travel, Bills, etc.).  
- Automatically refreshes when new expenses are added or deleted.

### 🧾 Add Expense Panel
- Add expenses with **Date, Category, Amount, and Description**.  
- **Smart Category Detection:** Automatically sets a category based on keywords:
  - `"uber"` or `"ola"` → *Travel*
  - `"lunch"` or `"zomato"` → *Food*
  - `"electricity"` or `"rent"` → *Bills*
- **OCR Integration:** Uses **Tess4J** to extract amounts from receipt images.

### 📜 View Expenses
- View all logged entries in a clean **table view**.  
- Delete any expense — data updates immediately and is auto-saved.

### ⚙️ Settings Panel
- One-click **manual backup** to Excel (`expenses.xlsx`).  
- Automatically loads previously saved expenses on startup.  
- Data remains persistent between app runs.

---

## 🧰 Tools & Technologies

| Category | Tools Used |
|-----------|-------------|
| **Language** | Java (JDK 17+ recommended) |
| **Framework** | JavaFX |
| **Build Tool** | Apache Maven |
| **IDE** | IntelliJ IDEA / VS Code / Eclipse |
| **Libraries Used** | `javafx-controls`, `javafx-fxml`, `tess4j`, `poi-ooxml` |
| **Storage** | Local persistence (`expenses.csv`) + Excel export (`expenses.xlsx`) |

---

## 🗂️ Project Structure

    ExpenseTrackerFX/
    │
    ├── pom.xml                        # Maven configuration file (dependencies, plugins)
    ├── README.md                      # Project overview (you already have this)
    ├── .gitignore                     # Ignored files list (I'll show below)
    ├── styles.css                     # Your custom app stylesheet (optional)
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/
    │   │   │       └── example/
    │   │   │           └── expensetrackerfx/
    │   │   │               ├── ExpenseTrackerFX.java      # Main Application class
    │   │   │               ├── Launcher.java              # JavaFX launcher class (optional)
    │   │   │               ├── FileStorage.java           # Handles CSV load/save
    │   │   │               ├── ExcelExporter.java         # Handles Excel export


---

## ⚙️ Setup & Installation

### 1️⃣ Clone the repository
```bash
    git clone https://github.com/Wild-Dandelion/ExpenseTrackerFX.git
    cd ExpenseTrackerFX


2️⃣ Open in IntelliJ IDEA (or VS Code / Eclipse)

    Ensure JDK 17+ is selected.
    
    Open the pom.xml file — Maven will download dependencies automatically.

3️⃣ Install Tesseract OCR
    
    Download from: https://github.com/tesseract-ocr/tesseract

🏁 Future Enhancements
    🔗 Integration with MySQL or SQLite
    ☁️ Cloud sync for multi-device access
    👥 User authentication

Author
  
  Developed by: Wild-Dandelion 
                tushary1212-cell
                sreehithreddy0202
                Sharanya-Dutta
📜 License

This project is licensed under the MIT License.
You are free to modify and distribute this project with proper attribution.
