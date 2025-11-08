package com.example.expensetrackerfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ExpenseTrackerFX extends Application {

    private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();
    private BorderPane rootPane;
    private StackPane contentArea;

    private Pane dashboardPanel;
    private Pane addExpensePanel;
    private Pane viewExpensesPanel;
    private Pane settingsPanel;

    private PieChart expensePieChart;
    private TableView<Expense> expenseTable;
    private final Label totalSpentLabel = new Label("₹0.00");
    private final Label monthlyAvgLabel = new Label("₹0.00");

    private final String ICON_DASHBOARD = "M12,2L2,7V17L12,22L22,17V7L12,2M11.1,15.7L8,12.6L9.4,11.2L11.1,12.9L14.6,9.4L16,10.8L11.1,15.7Z";
    private final String ICON_ADD = "M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z";
    private final String ICON_LIST = "M3,4H21V6H3V4M3,14H21V16H3V14M3,9H21V11H3V9M3,19H21V21H3V19Z";
    private final String ICON_SETTINGS = "M12,8A4,4 0 0,1 16,12A4,4 0 0,1 12,16A4,4 0 0,1 8,12A4,4 0 0,1 12,8Z";

    @Override
    public void start(Stage stage) {
        rootPane = new BorderPane();
        contentArea = new StackPane();

        rootPane.setTop(createHeader());
        rootPane.setLeft(createNavigationPanel());
        rootPane.setCenter(contentArea);

        initializeChart();
        initializeTable();

        dashboardPanel = createDashboardPanel();
        addExpensePanel = createAddExpensePanel();
        viewExpensesPanel = createViewExpensesPanel();
        settingsPanel = createSettingsPanel();

        // 🔹 Load previous data from file before showing the dashboard
        FileStorage.loadExpenses(expenseList);
        updateDashboard();

        contentArea.getChildren().add(dashboardPanel);

        Scene scene = new Scene(rootPane, 1200, 800);
        applyGlobalStyles(scene);
        stage.setScene(scene);
        stage.setTitle("Smart Expense Tracker");
        stage.show();

        stage.setOnCloseRequest(e -> FileStorage.saveExpenses(expenseList)); // autosave on exit
    }

    private HBox createHeader() {
        Text appTitle = new Text("Smart Expense Tracker");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        appTitle.setFill(Color.WHITE);

        Text userName = new Text("Welcome Back!");
        userName.setFont(Font.font("Segoe UI", 16));
        userName.setFill(Color.LIGHTGRAY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20, appTitle, spacer, userName);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #212529;");
        return header;
    }

    private VBox createNavigationPanel() {
        VBox navPanel = new VBox(10);
        navPanel.setPadding(new Insets(20));
        navPanel.setPrefWidth(220);
        navPanel.setStyle("-fx-background-color: #343A40;");
        navPanel.setAlignment(Pos.TOP_CENTER);

        Button dashboardBtn = createNavButton("Dashboard", ICON_DASHBOARD);
        Button addExpenseBtn = createNavButton("Add Expense", ICON_ADD);
        Button viewExpensesBtn = createNavButton("View Expenses", ICON_LIST);
        Button settingsBtn = createNavButton("Settings", ICON_SETTINGS);

        dashboardBtn.setOnAction(e -> switchPanel("Dashboard"));
        addExpenseBtn.setOnAction(e -> switchPanel("Add Expense"));
        viewExpensesBtn.setOnAction(e -> switchPanel("View Expenses"));
        settingsBtn.setOnAction(e -> switchPanel("Settings"));

        navPanel.getChildren().addAll(dashboardBtn, addExpenseBtn, viewExpensesBtn, settingsBtn);
        return navPanel;
    }

    private Button createNavButton(String text, String svgPath) {
        javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.WHITE);

        Label label = new Label(text);
        label.setTextFill(Color.WHITE);

        HBox content = new HBox(10, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(content);
        button.setPrefWidth(200);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #495057;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent;"));

        return button;
    }




    private Pane createDashboardPanel() {
        VBox dashboard = new VBox(25);
        dashboard.setPadding(new Insets(30));

        HBox summaryCards = new HBox(20,
                createSummaryCard("Total Spent", totalSpentLabel),
                createSummaryCard("Monthly Average", monthlyAvgLabel)
        );

        VBox.setVgrow(expensePieChart, Priority.ALWAYS);
        dashboard.getChildren().addAll(summaryCards, expensePieChart);
        return dashboard;
    }

    private Pane createAddExpensePanel() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList("Food", "Travel", "Bills", "Groceries", "Entertainment", "Other"));
        TextField amountField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("e.g., Lunch with colleagues, Uber ride...");
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> {
            String lowerCaseText = newVal.toLowerCase();
            if (lowerCaseText.contains("uber") || lowerCaseText.contains("ola")) {
                categoryBox.setValue("Travel");
            } else if (lowerCaseText.contains("lunch") || lowerCaseText.contains("zomato") || lowerCaseText.contains("swiggy")) {
                categoryBox.setValue("Food");
            } else if (lowerCaseText.contains("electricity") || lowerCaseText.contains("rent") || lowerCaseText.contains("water bill")) {
                categoryBox.setValue("Bills");
            } else if (lowerCaseText.contains("amazon") || lowerCaseText.contains("grocery")) {
                categoryBox.setValue("Groceries");
            } else if (lowerCaseText.contains("netflix") || lowerCaseText.contains("movie") || lowerCaseText.contains("spotify")) {
                categoryBox.setValue("Entertainment");
            }
        });

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryBox, 1, 1);
        grid.add(new Label("Amount (₹):"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);

        Button saveButton = new Button("Save Expense");
        saveButton.getStyleClass().add("success-button");

        Button ocrButton = new Button("Scan Receipt");
        ocrButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Receipt Image");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                String text = OCRUtility.extractText(file);
                System.out.println("=== RAW OCR TEXT ===");
                System.out.println(text);

                String amount = extractAmount(text);
                if (!amount.isEmpty()) {
                    amountField.setText(amount);
                    descriptionArea.setText("Extracted from receipt OCR");
                    showAlert("OCR Result", "Detected amount: ₹" + amount);
                } else {
                    showAlert("OCR Result", "Could not detect a valid amount in the image.");
                }
            }
        });

        saveButton.setOnAction(e -> {
            try {
                if (datePicker.getValue() == null || categoryBox.getValue() == null || amountField.getText().isEmpty()) {
                    showAlert("Validation Error", "Please fill in Date, Category, and Amount.");
                    return;
                }
                Expense newExpense = new Expense(
                        datePicker.getValue(),
                        categoryBox.getValue(),
                        Double.parseDouble(amountField.getText()),
                        descriptionArea.getText()
                );
                expenseList.add(newExpense);
                FileStorage.saveExpenses(expenseList); // auto-save every time
                updateDashboard();
                showAlert("Success", "Expense logged successfully!");
                amountField.clear();
                descriptionArea.clear();
                categoryBox.setValue(null);
                datePicker.setValue(LocalDate.now());
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid number for the amount.");
            }
        });

        HBox buttonBar = new HBox(15, saveButton, ocrButton);
        VBox panel = new VBox(25, new Label("Log New Expense"), grid, buttonBar);
        panel.setPadding(new Insets(30));
        return panel;
    }

    private Pane createViewExpensesPanel() {
        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> {
            Expense selected = expenseTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                expenseList.remove(selected);
                FileStorage.saveExpenses(expenseList);
                updateDashboard();
            }
        });

        VBox panel = new VBox(20, new Label("Expense History"), expenseTable, deleteButton);
        VBox.setVgrow(expenseTable, Priority.ALWAYS);
        panel.setAlignment(Pos.TOP_RIGHT);
        panel.setPadding(new Insets(30));
        return panel;
    }

    private Pane createSettingsPanel() {
        Button exportButton = new Button("Save Data to Excel File");
        exportButton.setOnAction(e -> {
            try {
                ExcelExporter.exportExpenses(expenseList);
                showAlert("Export Complete", "All current expenses have been exported to 'expenses.xlsx'.");
            } catch (Exception ex) {
                showAlert("Export Failed", "An error occurred while exporting data.");
                ex.printStackTrace();
            }
        });


        VBox panel = new VBox(20, new Label("Settings & Data Backup"), exportButton);
        panel.setPadding(new Insets(30));
        return panel;
    }

    private void initializeChart() {
        expensePieChart = new PieChart();
        expensePieChart.setTitle("Spending by Category");
        expensePieChart.setLegendSide(Side.LEFT);
    }

    private void initializeTable() {
        expenseTable = new TableView<>();
        TableColumn<Expense, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount (₹)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Expense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        expenseTable.getColumns().addAll(dateCol, categoryCol, amountCol, descCol);
        expenseTable.setItems(expenseList);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void updateDashboard() {
        Map<String, Double> categoryTotals = expenseList.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, total) -> pieChartData.add(new PieChart.Data(category, total)));
        expensePieChart.setData(pieChartData);

        DecimalFormat df = new DecimalFormat("₹ #,##0.00");
        double total = expenseList.stream().mapToDouble(Expense::getAmount).sum();
        long months = expenseList.stream().map(e -> e.getDate().withDayOfMonth(1)).distinct().count();
        double average = (months == 0) ? 0 : total / months;

        totalSpentLabel.setText(df.format(total));
        monthlyAvgLabel.setText(df.format(average));
    }

    private void switchPanel(String panelName) {
        contentArea.getChildren().clear();
        switch (panelName) {
            case "Dashboard" -> contentArea.getChildren().add(dashboardPanel);
            case "Add Expense" -> contentArea.getChildren().add(addExpensePanel);
            case "View Expenses" -> contentArea.getChildren().add(viewExpensesPanel);
            case "Settings" -> contentArea.getChildren().add(settingsPanel);
        }
    }

    private VBox createSummaryCard(String title, Label dataLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        dataLabel.getStyleClass().add("card-data");

        VBox card = new VBox(10, titleLabel, dataLabel);
        card.getStyleClass().add("summary-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private String extractAmount(String text) {
        Pattern pattern = Pattern.compile("(\\d+[.,]\\d{2})");
        Matcher matcher = pattern.matcher(text);
        double max = 0;
        while (matcher.find()) {
            try {
                double value = Double.parseDouble(matcher.group(1).replace(",", ""));
                if (value > max && value < 100000) max = value;
            } catch (NumberFormatException ignored) {}
        }
        return max == 0 ? "" : String.format("%.2f", max);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void applyGlobalStyles(Scene scene) {
        String css = """
            .root { -fx-font-family: 'Segoe UI'; -fx-background-color: #495057; }
            .label { -fx-text-fill: #F8F9FA; -fx-font-size: 14px; }
            .button { -fx-background-radius: 5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; }
            .success-button { -fx-background-color: #198754; }
            .danger-button { -fx-background-color: #DC3545; }
            .summary-card { -fx-background-color: #343A40; -fx-padding: 20; -fx-background-radius: 8; }
            .card-title { -fx-font-size: 16px; -fx-text-fill: #ADB5BD; }
            .card-data { -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; }
        """;
        scene.getStylesheets().add("data:text/css," + java.net.URLEncoder.encode(css, java.nio.charset.StandardCharsets.UTF_8));
    }

    public static class Expense {
        private final LocalDate date;
        private final String category;
        private final double amount;
        private final String description;

        public Expense(LocalDate date, String category, double amount, String description) {
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.description = description;
        }

        public LocalDate getDate() { return date; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
    }

    public static class OCRUtility {
        public static String extractText(File imageFile) {
            try {
                Tesseract tesseract = new Tesseract();
                tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
                tesseract.setLanguage("eng");
                BufferedImage img = ImageIO.read(imageFile);
                if (img == null) return "Error: Could not read image file.";
                return tesseract.doOCR(img);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error reading image.";
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
