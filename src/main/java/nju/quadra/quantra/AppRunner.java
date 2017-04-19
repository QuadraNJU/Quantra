package nju.quadra.quantra;

import javafx.application.Application;
import javafx.stage.Stage;
import nju.quadra.quantra.ui.SplashVC;

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
