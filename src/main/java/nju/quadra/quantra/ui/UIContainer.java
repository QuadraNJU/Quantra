package nju.quadra.quantra.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by adn55 on 2017/3/8.
 */
public class UIContainer extends Stage {

    @FXML
    private StackPane rootStack;
    @FXML
    private ScrollPane contentPane;
    @FXML
    private Pane loadingPane, paneCompare;
    private static ScrollPane contentPaneS;
    private static Pane loadingPaneS;
    @FXML
    private VBox paneCompareList;
    public static VBox paneCompareListS;
    private static int loadingCount = 0;

    public UIContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/container.fxml"));
            loader.setController(this);
            this.setScene(new Scene(loader.load()));
            contentPaneS = contentPane;
            loadingPaneS = loadingPane;
            paneCompareListS = paneCompareList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadContent(Node node) {
        Platform.runLater(() -> contentPaneS.setContent(node));
    }

    public static void showLoading() {
        contentPaneS.setVisible(false);
        loadingPaneS.setVisible(true);
        loadingCount++;
    }

    public static void hideLoading() {
        loadingCount--;
        if (loadingCount <= 0) {
            loadingCount = 0;
            loadingPaneS.setVisible(false);
            contentPaneS.setVisible(true);
        }
    }

    @FXML
    private void onMarketPageAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new MarketVC());
                hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onStockListAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new StockListVC());
                hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onCompareAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new StockCompareVC());
                paneCompare.setVisible(false);
                hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onComparePageAction() throws IOException {
        loadCompareList();
        paneCompare.setVisible(true);
    }

    @FXML
    private void onCompareCancelAction() {
        paneCompare.setVisible(false);
    }

    @FXML
    private void onCompareCleanAction() throws IOException {
        StockCompareVC.chosenStocks.clear();
        loadCompareList();
        StockCompareVC.load();
        paneCompare.setVisible(true);
    }

    public static void loadCompareList() throws IOException {
        paneCompareListS.getChildren().clear();
        for (int i = 0; i < StockCompareVC.chosenStocks.size(); i++) {
            paneCompareListS.getChildren().add(new CompareSmallWindowItemVC(StockCompareVC.chosenStocks.get(i)));
        }
    }
}
