package iris.playharmony.view;

import iris.playharmony.controller.DatabaseController;
import iris.playharmony.controller.NavController;
import iris.playharmony.exceptions.CreateUserException;
import iris.playharmony.exceptions.EmailException;
import iris.playharmony.model.Email;
import iris.playharmony.model.ObservableUser;
import iris.playharmony.model.Role;
import iris.playharmony.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.stream.Collectors;

import static iris.playharmony.util.TypeUtils.initSingleton;

public class UserListView extends BorderPane {
    private static int SPACING = 15;
    private static Font TITLE_FONT = new Font("Arial", 18);
    private static Font FIELD_FONT = new Font("Arial", 14);
    private static final int ROWS_PER_PAGE = 20;

    private HeaderView headerView;
    private NavigationView navigationView;
    private NavController navController;
    private FooterView footerView;

    public UserListView() {
        headerView = new HeaderView();

        navigationView = new NavigationView();
        navigationView.setView(new UserListViewNavigation());
        navController = new NavController(navigationView);

        footerView = new FooterView();

        setTop(headerView);
        setCenter(navigationView);
        setBottom(footerView);

        initSingleton(UserView.UserViewNavigation.class, navController);
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public class UserListViewNavigation extends VBox {

        private TextField name = new TextField();
        private TextField surname = new TextField();
        private TextField email = new TextField();
        private TextField category = new TextField();
        private TableView usersTable = new TableView<>();
        private ComboBox<Object> role = new ComboBox<>();

        ObservableList<ObservableUser> data = FXCollections.observableArrayList();

        public UserListViewNavigation() {
            super(SPACING);
            add(getTitleRow());
            initializeTableView();
            add(getPagination());
            add(getBottomButtonPanel());
            setPadding(new Insets(SPACING));
        }

        private Node add(Node node) {
            getChildren().add(node);

            return node;
        }

        private Node getTitleRow() {
            HBox titleRow = new HBox(title("Users"));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            titleRow.getChildren().add(region);
            titleRow.getChildren().add(button("Add User", event -> {
                navController.clear();
                navController.pushView(new UserView().getNavigationView());
            }));

            return titleRow;
        }

        private Node getBottomButtonPanel() {
            Region padding = new Region();
            padding.setPrefWidth(5);
            HBox bottomButtonPanel = new HBox(button("Remove User", this::removeUser),
                    padding,
                    button("Update User", event ->{
                        navController.clear();
                        ObservableUser selectedItem = (ObservableUser) usersTable.getSelectionModel().getSelectedItem();
                        if(selectedItem != null)
                            navController.pushView(new UpdateUserView(selectedItem).getNavigationView());
                    }));
            return bottomButtonPanel;
        }
        private Label title(String text) {
            Label title = new Label(text);
            title.setFont(TITLE_FONT);
            return title;
        }

        private void removeUser(Event event) {
            event.consume();
            ObservableUser selection = (ObservableUser) usersTable.getSelectionModel().getSelectedItem();
            if(selection == null)
                return;
            new DatabaseController().removeUser(selection.getEmail());
            updateTableViewData();
        }

        private Node textFieldLabeled(TextField textField, String text) {
            VBox panel = new VBox();

            Label label = new Label(text);
            label.setFont(FIELD_FONT);

            panel.getChildren().addAll(label, textField);

            return panel;
        }

        private TableView initializeTableView() {
            usersTable.setEditable(false);
            initializeColumns(usersTable);
            updateTableViewData();
            return usersTable;
        }

        private TableView updateTableViewData() {
            data = mockUsers();
            usersTable.setItems(data);
            usersTable.refresh();
            return usersTable;
        }

        private ObservableList<ObservableUser> getDBData() {
            data = FXCollections.observableArrayList();
            new DatabaseController().getUsers().stream()
                    .map(ObservableUser::from)
                    .forEach(data::add);
            return data;
        }

        private Pagination getPagination() {
            Pagination pagination = new Pagination((data.size() / ROWS_PER_PAGE + 1), 0);
            pagination.setPageFactory(this::createPage);
            return pagination;
        }

        private Node createPage(int pageIndex) {

            int fromIndex = pageIndex * ROWS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, data.size());
            usersTable.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));

            return new BorderPane(usersTable);
        }
        private TableView initializeColumns(TableView tableView) {
            TableColumn imageColumn = new TableColumn("Photo");
            imageColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
            imageColumn.setPrefWidth(100);
            TableColumn nameColumn = new TableColumn("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn surnameColumn = new TableColumn("Surname");
            surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            TableColumn emailColumn = new TableColumn("Email");
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn categoryColumn = new TableColumn("Category");
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            TableColumn roleColumn = new TableColumn("Role");
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

            usersTable.getColumns().addAll(imageColumn, nameColumn, surnameColumn, emailColumn, categoryColumn, roleColumn);
            usersTable.getColumns().forEach(column -> ((TableColumn)column).setStyle("-fx-alignment: CENTER;"));
            usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            return usersTable;
        }

        private ObservableList<ObservableUser> mockUsers() {
            ObservableList<ObservableUser> users = FXCollections.observableArrayList();
            users.add(ObservableUser.from(new User().name("test")
                    .role(Role.STUDENT)
                    .surname("test2")
                    .category("testcat")
                    .mail(new Email("test", "test.test"))
                    .photo(new File("C:\\Users\\omark\\OneDrive\\Pictures\\eva.jpg"))));
            users.add(ObservableUser.from(new User().name("test")
                    .role(Role.TEACHER)
                    .surname("test2")
                    .category("testcat")
                    .mail(new Email("test", "test.test"))
                    .photo(new File("C:\\Users\\omark\\OneDrive\\Pictures\\eva.jpg"))));
            users.add(ObservableUser.from(new User().name("test")
                    .role(Role.ADMIN)
                    .surname("test2")
                    .category("testcat")
                    .mail(new Email("test", "test.test"))
                    .photo(new File("C:\\Users\\omark\\OneDrive\\Pictures\\eva.jpg"))));
            users.add(ObservableUser.from(new User().name("test")
                    .role(Role.STUDENT)
                    .surname("test2")
                    .category("testcat")
                    .mail(new Email("test", "test.test"))
                    .photo(new File("C:\\Users\\omark\\OneDrive\\Pictures\\eva.jpg"))));
            return users;
        }

        private Node buttonWithResult(TextField textField, String labelText, String buttonText, EventHandler<ActionEvent> event) {
            Label photoText = new Label(labelText);
            photoText.setFont(FIELD_FONT);

            HBox panel = new HBox();

            textField.setDisable(true);

            Button button = new Button(buttonText);
            button.setOnAction(event);
            button.setBackground(new Background(new BackgroundFill(Color.rgb( 174, 214, 241 ), CornerRadii.EMPTY, Insets.EMPTY)));

            panel.getChildren().addAll(textField, button);

            return panel;
        }

        private Node button(String text, EventHandler<ActionEvent> event) {
            Button button = new Button(text);
            button.setOnAction(event);
            button.setBackground(new Background(new BackgroundFill(Color.rgb( 174, 214, 241 ), CornerRadii.EMPTY, Insets.EMPTY)));

            return button;
        }


        private void errorAlert(String title, String text) {
            Alert emailErrorDialog = new Alert(Alert.AlertType.ERROR);
            emailErrorDialog.setTitle(title);
            emailErrorDialog.setHeaderText(text);
            emailErrorDialog.initStyle(StageStyle.UTILITY);
            java.awt.Toolkit.getDefaultToolkit().beep();
            emailErrorDialog.showAndWait();
        }
    }
}