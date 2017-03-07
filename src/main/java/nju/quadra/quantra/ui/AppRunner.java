package nju.quadra.quantra.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by adn55 on 2017/3/7.
 */
public class AppRunner extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new SplashVC().show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
