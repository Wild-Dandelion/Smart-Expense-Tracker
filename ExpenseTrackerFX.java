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

    import java.text.DecimalFormat;
    import java.time.LocalDate;
    import java.util.Map;
    import java.util.stream.Collectors;

    public class ExpenseTrackerFX extends Application {

        // local data model and database for user info
        private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();

        // for Border of the window and pane
        private BorderPane rootPane;
        private StackPane contentArea;

        // for the buttons that we used
        private Pane dashboardPanel;
        private Pane addExpensePanel;
        private Pane viewExpensesPanel;
        private Pane settingsPanel;

        // sahred components shared between classes
        private PieChart expensePieChart;
        private TableView<Expense> expenseTable;
        private final Label totalSpentLabel = new Label("₹0.00"); // Initialized as a class member
        private final Label monthlyAvgLabel = new Label("₹0.00"); // Initialized as a class member

        // icons for the panels
        private final String ICON_DASHBOARD = "M12,2L2,7V17L12,22L22,17V7L12,2M11.1,15.7L8,12.6L9.4,11.2L11.1,12.9L14.6,9.4L16,10.8L11.1,15.7Z";
        private final String ICON_ADD = "M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z";
        private final String ICON_LIST = "M3,4H21V6H3V4M3,14H21V16H3V14M3,9H21V11H3V9M3,19H21V21H3V19Z";
        private final String ICON_SETTINGS = "M12,8A4,4 0 0,1 16,12A4,4 0 0,1 12,16A4,4 0 0,1 8,12A4,4 0 0,1 12,8M12,10A2,2 0 0,0 10,12A2,2 0 0,0 12,14A2,2 0 0,0 14,12A2,2 0 0,0 12,10M10,22C9.75,22 9.5,21.9 9.3,21.7L4.6,17C4.2,16.6 4,16.1 4,15.6V8.4C4,7.9 4.2,7.4 4.6,7L9.3,2.3C9.7,1.9 10.2,1.7 10.7,1.7H13.2C13.8,1.7 14.3,1.9 14.7,2.3L19.4,7C19.8,7.4 20,7.9 20,8.4V15.6C20,16.1 19.8,16.6 19.4,17L14.7,21.7C14.5,21.9 14.25,22 14,22H10Z";

        @Override
        public void start(Stage stage) {
            rootPane = new BorderPane();
            contentArea = new StackPane();

            // to set UI sections ---
            rootPane.setTop(createHeader());
            rootPane.setLeft(createNavigationPanel());
            rootPane.setCenter(contentArea);

            // to initialize shared components FIRST
            initializeChart();
            initializeTable();

            // to create main content panels ONCE
            dashboardPanel = createDashboardPanel();
            addExpensePanel = createAddExpensePanel();
            viewExpensesPanel = createViewExpensesPanel();
            settingsPanel = createSettingsPanel();

            // --- Set initial view ---
            contentArea.getChildren().add(dashboardPanel);

            // to set SCENE & STAGE SETUP ---
            Scene scene = new Scene(rootPane, 1200, 800);
            applyGlobalStyles(scene);
            stage.setScene(scene);
            stage.setTitle("Smart Expense Tracker");
            stage.show();

            // Add sample data and update the UI
            addSampleData();
            updateDashboard();
        }

        // UI Creation Method

        private HBox createHeader() {
            Text appTitle = new Text("Smart Expense Tracker");
            appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
            appTitle.setFill(Color.WHITE);

            Text userName = new Text("Welcome, Sreehith!");
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

        private Pane createDashboardPanel() {
            VBox dashboard = new VBox(25);
            dashboard.setPadding(new Insets(30));

            // to show the card present inside the Dashboard panels
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
                if (lowerCaseText.contains("uber") || lowerCaseText.contains("ola")) { categoryBox.setValue("Travel"); }
                else if (lowerCaseText.contains("lunch") || lowerCaseText.contains("zomato")) { categoryBox.setValue("Food"); }
                else if (lowerCaseText.contains("electricity") || lowerCaseText.contains("rent")) { categoryBox.setValue("Bills"); }
            });

            grid.add(new Label("Date:"), 0, 0); grid.add(datePicker, 1, 0);
            grid.add(new Label("Category:"), 0, 1); grid.add(categoryBox, 1, 1);
            grid.add(new Label("Amount (₹):"), 0, 2); grid.add(amountField, 1, 2);
            grid.add(new Label("Description:"), 0, 3); grid.add(descriptionArea, 1, 3);

            Button saveButton = new Button("Save Expense");
            saveButton.getStyleClass().add("success-button");

            Button ocrButton = new Button("Scan Receipt (Simulated)");
            ocrButton.setOnAction(e -> {
                datePicker.setValue(LocalDate.of(2025, 10, 17));
                categoryBox.setValue("Food");
                amountField.setText("450.75");
                descriptionArea.setText("Simulated: Lunch at A2B Restaurant");
                showAlert("OCR Simulation", "Expense details extracted from a sample receipt.");
            });

            HBox buttonBar = new HBox(15, saveButton, ocrButton);

            saveButton.setOnAction(e -> {
                try {
                    if (datePicker.getValue() == null || categoryBox.getValue() == null || amountField.getText().isEmpty()) {
                        showAlert("Validation Error", "Please fill in Date, Category, and Amount."); return;
                    }
                    expenseList.add(new Expense(datePicker.getValue(), categoryBox.getValue(), Double.parseDouble(amountField.getText()), descriptionArea.getText()));
                    updateDashboard();
                    datePicker.setValue(LocalDate.now()); categoryBox.setValue(null); amountField.clear(); descriptionArea.clear();
                    showAlert("Success", "Expense logged successfully!");
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Please enter a valid number for the amount.");
                }
            });

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
            Button exportButton = new Button("Export Report (Simulated)");
            exportButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report Export Simulation");
                alert.setHeaderText("Export Successful (Simulated)");
                alert.setContentText("A PDF/Excel report of all " + expenseList.size() + " expenses would be generated and saved.");
                alert.showAndWait();
            });

            VBox panel = new VBox(20, new Label("Settings & Reports"), exportButton);
            panel.setPadding(new Insets(30));
            return panel;
        }


        //Logic behind the fucntions and the formulas for avg month and total expense

        // CORRECTED: This method now switches between existing panels instead of creating new ones.
        private void switchPanel(String panelName) {
            contentArea.getChildren().clear();
            Pane panelToShow = null;
            switch (panelName) {
                case "Dashboard":
                    updateDashboard(); // to update the stats jst before showing the panel
                    panelToShow = dashboardPanel;
                    break;
                case "Add Expense":
                    panelToShow = addExpensePanel;
                    break;
                case "View Expenses":
                    panelToShow = viewExpensesPanel;
                    break;
                case "Settings":
                    panelToShow = settingsPanel;
                    break;
            }
            if (panelToShow != null) {
                contentArea.getChildren().add(panelToShow);
            }
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

        private void addSampleData() {
            expenseList.addAll(
                    new Expense(LocalDate.of(2025, 10, 1), "Groceries", 3500.50, "Monthly groceries"),
                    new Expense(LocalDate.of(2025, 10, 5), "Bills", 1200.00, "Electricity Bill"),
                    new Expense(LocalDate.of(2025, 10, 12), "Travel", 750.00, "Uber to airport"),
                    new Expense(LocalDate.of(2025, 9, 28), "Entertainment", 980.00, "Movie tickets")
            );
        }
        // for the  Styling and the UI elements we used in the project

        private Button createNavButton(String text, String svgPath) {
            javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
            icon.setContent(svgPath);
            icon.setFill(Color.WHITE);

            Label label = new Label(text);
            HBox content = new HBox(15, icon, label);
            content.setAlignment(Pos.CENTER_LEFT);

            Button button = new Button();
            button.setGraphic(content);
            button.setPrefWidth(200);
            button.setAlignment(Pos.CENTER_LEFT);
            button.getStyleClass().add("nav-button");
            return button;
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

        private void showAlert(String title, String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }

        private void applyGlobalStyles(Scene scene) {
            String css = """
                .root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-background-color: #495057; }
                .label { -fx-text-fill: #F8F9FA; -fx-font-size: 14px; }
                .text-field, .text-area, .combo-box, .date-picker { -fx-font-size: 14px; -fx-pref-height: 40px; -fx-background-color: #495057; -fx-text-fill: white; -fx-prompt-text-fill: #ADB5BD; -fx-border-color: #6C757D; -fx-border-radius: 5; -fx-background-radius: 5; }
                .text-area { -fx-pref-height: 100px; }
                .combo-box .list-cell { -fx-background-color: #495057; -fx-text-fill: white; }
                .date-picker .text-field { -fx-background-color: transparent; }
                .button { -fx-background-radius: 5; -fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; }
                .nav-button { -fx-background-color: transparent; -fx-alignment: center-left; -fx-padding: 12 15; }
                .nav-button:hover { -fx-background-color: #495057; }
                .success-button { -fx-background-color: #198754; }
                .success-button:hover { -fx-background-color: #157347; }
                .danger-button { -fx-background-color: #DC3545; }
                .danger-button:hover { -fx-background-color: #BB2D3B; }
                .summary-card { -fx-background-color: #343A40; -fx-padding: 20; -fx-background-radius: 8; }
                .card-title { -fx-font-size: 16px; -fx-text-fill: #ADB5BD; }
                .card-data { -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; }
                .chart-title { -fx-text-fill: white; -fx-font-size: 18px; }
                .chart-legend { -fx-background-color: transparent; }
                .chart-legend-item { -fx-text-fill: #E0E0E0; }
                .table-view { -fx-background-color: #343A40; -fx-border-color: #6C757D; -fx-border-radius: 5; -fx-background-radius: 5; }
                .table-view .column-header-background { -fx-background-color: #212529; }
                .table-view .column-header .label { -fx-text-fill: white; -fx-font-weight: bold; }
                .table-view .table-cell { -fx-text-fill: #F8F9FA; -fx-padding: 10; }
                .table-row-cell { -fx-background-color: #343A40; }
                .table-row-cell:odd { -fx-background-color: #495057; }
                .table-row-cell:selected { -fx-background-color: #0D6EFD; }
            """;
            scene.getStylesheets().add("data:text/css," + java.net.URLEncoder.encode(css, java.nio.charset.StandardCharsets.UTF_8));
        }


        // contents that will contain inside Data Model Class
        public static class Expense {
            private final LocalDate date;
            private final String category;
            private final double amount;
            private final String description;

            public Expense(LocalDate date, String category, double amount, String description) {
                this.date = date; this.category = category; this.amount = amount; this.description = description;
            }

            public LocalDate getDate() { return date; }
            public String getCategory() { return category; }
            public double getAmount() { return amount; }
            public String getDescription() { return description; }
        }

        // entry point for the application
        public static void main(String[] args) {
            launch();
        }
    }