package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDecorator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by adn55 on 2017/3/8.
 */
public class UIContainer extends Stage {

    @FXML
    private StackPane rootStack;
    @FXML
    private Pane contentPane;

    public UIContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/container.fxml"));
            loader.setController(this);
            JFXDecorator decorator = new JFXDecorator(this, loader.load());
            decorator.setMaximized(true);
            this.setScene(new Scene(decorator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    @FXML
    private void onMarketPageAction() {
        try {
            loadContent(new MarketVC());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
