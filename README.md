Smart Expense Tracker (JavaFX)

A modern, desktop expense tracker application built with JavaFX. It provides a clean, dark-themed interface to log, view, and analyze your spending.
<img width="1503" height="1020" alt="image" src="https://github.com/user-attachments/assets/f898ece9-05e1-4ef6-9e12-7742f44ceaaa" />


Features

Modern UI: A beautiful, responsive styled completely with CSS.

Dashboard: At-a-glance dashboard with summary cards for "Total Spent" and "Monthly Average," plus a PieChart visualizing spending by category.

Add Expenses: An intuitive form to add new expenses, featuring a DatePicker, ComboBox for categories, and text fields.

Smart Suggestions: The category is automatically suggested based on keywords entered in the description (e.g., "uber" suggests "Travel").

View & Manage: A TableView lists all expenses, allowing you to select and delete entries.

Simulations: Includes simulated "Scan Receipt (OCR)" and "Export Report" buttons to demonstrate potential advanced features.

Single-File Application: All logic, UI, styling, and the data model are contained within a single .java file.

Requirements

  Java JDK 11+: (Java Development Kit)
  
  JavaFX SDK 11+: (Software Development Kit). This is no longer bundled with the standard JDK and must be downloaded separately.

How to Run

  There are two main ways to run this application: from an IDE (Recommended) or from the command line.
  
  Option 1: Running from an IDE (Recommended)

Configure Project:

  Create a new Java project in your IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
  
  Add the ExpenseTrackerFX.java file to your project's src directory (e.g., src/com/example/expensetrackerfx/ExpenseTrackerFX.java).

Add JavaFX Library:

  Download the JavaFX SDK from the Gluon website.

  Add the JavaFX SDK as a global or project library. In IntelliJ, this is under File > Project Structure > Libraries. Point it to the lib folder of the SDK.

Set VM Options:

  You must tell the Java VM where to find the JavaFX modules.

  Find the "Run Configuration" for your ExpenseTrackerFX class.
  
  Add the following to the "VM options" field. Remember to replace /path/to/javafx-sdk/lib with the actual path on your computer.
  
      --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.charts


Run:

Run the main method in ExpenseTrackerFX.

Option 2: Running from the Command Line

Prerequisites:

Ensure javac and java are in your system's PATH.

Download the JavaFX SDK and note the path to its lib folder.

Compile the Code:

Open a terminal and navigate to the directory containing your com folder.

Run the javac command, providing the module path and specifying the modules needed.

# Make sure to replace /path/to/javafx-sdk/lib

    javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.charts com/example/expensetrackerfx/ExpenseTrackerFX.java


Run the Application:

Run the java command, again providing the module path and the fully qualified class name.

# Make sure to replace /path/to/javafx-sdk/lib

    java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.charts com.example.expensetrackerfx.ExpenseTrackerFX


Project Files

    .
    └── com/
        └── example/
            └── expensetrackerfx/
                └── ExpenseTrackerFX.java    # The main application file
