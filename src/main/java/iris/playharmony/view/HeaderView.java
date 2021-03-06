package iris.playharmony.view;

import iris.playharmony.controller.NavController;
import iris.playharmony.model.User;
import iris.playharmony.session.Session;
import iris.playharmony.util.*;
import iris.playharmony.view.main.LobbyView;
import iris.playharmony.view.session.LoginView;
import iris.playharmony.view.util.AlertFactory;
import iris.playharmony.view.util.ButtonFactory;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeaderView extends HBox implements NavigationViewChangedListener {

    private static final String TITLE = "PlayHarmony";
    private static final Font TITLE_FONT = new Font("Arial", 28);
    private static final Font ROLE_FONT = new Font("Arial", 28);

    private static final AtomicBoolean ALREADY_INSTANCED = new AtomicBoolean();


    HeaderView() {

        if(!ALREADY_INSTANCED.compareAndSet(false, true)) {
            throw new IllegalStateException(getClass().getSimpleName() + " must not be instantiated more than once!");
        }

        setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(Color.rgb(174, 214, 241), CornerRadii.EMPTY, Insets.EMPTY)));
        setPadding(new Insets(20));

        refresh();
    }

    private void refresh() {

        getChildren().clear();

        add(createBackButton());
        add(setTitle());
        add(getRegion());
        add(setUserRegion());
    }

    private void add(Node node) {
        getChildren().add(node);
    }

    private Label setTitle() {

        Label leftTitle = new Label(TITLE);

        leftTitle.setFont(TITLE_FONT);

        leftTitle.setTranslateX(50);

        return leftTitle;
    }

    private Region setUserRegion() {

        final User user = Session.getSession().currentUser();

        if(user == null) {
            return createNoLoggedInUserRegion();
        }

        return createLoggedInUserRegion(user);
    }

    private Region createNoLoggedInUserRegion() {

        HBox region = new HBox(5);

        Button loginButton = ButtonFactory.button("Log in", this::onLoginButtonClicked);
        loginButton.setFont(Font.font(20));
        loginButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        region.getChildren().add(loginButton);

        return region;
    }

    private Region createLoggedInUserRegion(User user) {

        HBox region = new HBox(100);
        region.setAlignment(Pos.CENTER_LEFT);

        Parent userProfileView = createUserProfileView(user);

        Button logoutButton = ButtonFactory.button("Log out", this::onLogoutButtonClicked);
        logoutButton.setFont(Font.font(20));
        logoutButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");

        region.getChildren().addAll(userProfileView, logoutButton);

        return region;
    }

    private Parent createUserProfileView(User user) {

        VBox container = new VBox(4);
        container.setAlignment(Pos.TOP_CENTER);

        CircleImage photo = new CircleImage(ImageFactory.loadFromFile(user.getPhoto().getAbsolutePath()));

        Label nameLabel = new Label(user.getName());
        nameLabel.setFont(Font.font(12));

        container.getChildren().addAll(photo, nameLabel);

        return container;
    }

    private void onLoginButtonClicked(ActionEvent actionEvent) {
        NavController.get().pushView(new LoginView());
    }

    private void onLogoutButtonClicked(ActionEvent actionEvent) {
        final boolean logout = AlertFactory.confirmAlert("Log out", "Are you sure you want to log out?");
        if(logout) {
            Session.getSession().setCurrentUser(null);
            NavController.get().setView(new LobbyView());
            refresh();
        }
    }

    private Region getRegion() {

        Region region = new Region();

        setHgrow(region, Priority.ALWAYS);

        return region;
    }

    private Node createBackButton() {

        Button button = ButtonFactory.imageButton(Resources.get("icons/player/back.png"));

        button.setOnAction(this::onBackButtonClicked);

        return button;
    }

    private void onBackButtonClicked(ActionEvent actionEvent) {

        Parent view = NavController.get().getCurrentView();

        if(view != null) {
            if(!Objects.equals(view.getClass(), LobbyView.class)) {
                NavController.get().popView();
                TypeUtils.callAnnotatedMethod(NavController.get().getCurrentView(), OnRefresh.class);
            }
        } else {
            NavController.get().setView(new LobbyView());
        }
    }

    @Override
    public void onViewChanged(Parent newView) {
        refresh();
    }
}
