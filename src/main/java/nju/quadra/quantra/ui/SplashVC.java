package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nju.quadra.quantra.data.StockData;

import java.io.IOException;

/**
 * Created by adn55 on 2017/3/7.
 */
public class SplashVC extends Stage {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXDialog errorDialog;
    @FXML
    private JFXButton okButton;

    public SplashVC() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/splash.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            this.setScene(new Scene(root));
            this.initStyle(StageStyle.TRANSPARENT);
            SplashVC _this = this;
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (StockData.getList().size() > 0) {
                        Platform.runLater(() -> {
                            new UIContainer().show();
                            _this.close();
                        });
                    } else {
                        Platform.runLater(() -> errorDialog.show(stackPane));
                    }
                    return null;
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onErrorDialogClosed() {
        Platform.exit();
    }

    @FXML
    private void onOkButtonAction() {
        errorDialog.close();
    }

}
