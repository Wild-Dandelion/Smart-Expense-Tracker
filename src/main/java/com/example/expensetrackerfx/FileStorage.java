package com.example.expensetrackerfx;

import javafx.collections.ObservableList;
import java.io.*;
import java.time.LocalDate;

public class FileStorage {
    private static final String FILE_PATH = "expenses.csv";

    public static void saveExpenses(ObservableList<ExpenseTrackerFX.Expense> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("Date,Category,Amount,Description");
            writer.newLine();
            for (ExpenseTrackerFX.Expense e : list) {
                writer.write(e.getDate() + "," + e.getCategory() + "," + e.getAmount() + "," + e.getDescription().replace(",", " "));
                writer.newLine();
            }
            System.out.println("✅ Expenses saved to file (" + list.size() + " entries)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadExpenses(ObservableList<ExpenseTrackerFX.Expense> list) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("ℹ️ No previous data found (" + FILE_PATH + ")");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine(); // skip header if present
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip blank lines
                String[] p = line.split(",", 4);
                if (p.length < 4 || p[0].isBlank()) {
                    System.out.println("⚠️ Skipping invalid line: " + line);
                    continue;
                }

                try {
                    LocalDate date = LocalDate.parse(p[0]);
                    String category = p[1];
                    double amount = Double.parseDouble(p[2]);
                    String desc = p[3];
                    list.add(new ExpenseTrackerFX.Expense(date, category, amount, desc));
                } catch (Exception ex) {
                    System.out.println("⚠️ Could not parse line: " + line);
                }
            }
            System.out.println("✅ Loaded " + list.size() + " expenses from file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
