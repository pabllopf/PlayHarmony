package iris.playharmony.view;

import iris.playharmony.controller.NavController;
import javafx.scene.layout.BorderPane;

import static iris.playharmony.util.TypeUtils.initSingleton;

public class MainView extends BorderPane {

    private HeaderView headerView;
    private NavigationView navigationView;
    private NavController navController;
    private FooterView footerView;

    public MainView() {

        headerView = new HeaderView();

        navigationView = new NavigationView();

        navController = new NavController(navigationView);

        footerView = new FooterView();

        setTop(headerView);

        setCenter(navigationView);

        setBottom(footerView);

        navigationView.getListeners().add(headerView);

        initSingleton(NavController.class, navController);
    }
}
