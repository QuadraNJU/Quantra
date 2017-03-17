package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/8.
 */
public class UIContainer extends Stage {

    @FXML
    private StackPane rootStack, contentPane;
    @FXML
    private Pane loadingPane, paneCompare, paneSearch;
    private static StackPane contentPaneS;
    private static Pane loadingPaneS;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private JFXListView searchList;
    @FXML
    private VBox paneCompareList;
    public static VBox paneCompareListS;
    private static int loadingCount = 0;

    public UIContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/container.fxml"));
            loader.setController(this);
            this.setScene(new Scene(loader.load()));
            this.setMaximized(true);
            searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
                String text = newValue.trim().toLowerCase();
                if (!text.isEmpty()) {
                    paneSearch.setVisible(true);
                    searchList.getItems().clear();
                    List<StockInfoPtr> result;
                    char first = text.charAt(0);
                    if (first >= '0' && first <= '9') {
                        int input = Integer.parseInt(text);
                        int lower = (int) (input * Math.pow(10, 6 - text.length()));
                        int upper = (int) ((input + 1) * Math.pow(10, 6 - text.length()) - 1);
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getCode() >= lower && ptr.get().getCode() <= upper).collect(Collectors.toList());
                    } else if (first >= 'a' && first <= 'z') {
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getPinyin().startsWith(text)).collect(Collectors.toList());
                    } else {
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getName().contains(text)).collect(Collectors.toList());
                    }
                    if (result != null) {
                        for (StockInfoPtr ptr : result) {
                            searchList.getItems().add(new Label(String.format("%06d", ptr.get().getCode()) + " " + ptr.get().getName()));
                        }
                    }
                }
            });
            contentPaneS = contentPane;
            loadingPaneS = loadingPane;
            paneCompareListS = paneCompareList;
            onMarketPageAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadContent(Node node) {
        Platform.runLater(() -> contentPaneS.getChildren().setAll(node));
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
    private void onSearchKeyTyped(KeyEvent t) {
        JFXTextField textField = (JFXTextField) t.getSource();

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
    private void clickToHide(MouseEvent t) {
        ((Node) t.getSource()).setVisible(false);
    }

    @FXML
    private void onCompareCleanAction() throws IOException {
        StockCompareVC.chosenStocks.clear();
        loadCompareList();
        StockCompareVC.load();
        paneCompare.setVisible(true);
    }

    @FXML
    private void onCompareCancelAction() {
        paneCompare.setVisible(false);
    }

    public static void loadCompareList() throws IOException {
        paneCompareListS.getChildren().clear();
        for (int i = 0; i < StockCompareVC.chosenStocks.size(); i++) {
            paneCompareListS.getChildren().add(new CompareSmallWindowItemVC(StockCompareVC.chosenStocks.get(i)));
        }
    }
}
