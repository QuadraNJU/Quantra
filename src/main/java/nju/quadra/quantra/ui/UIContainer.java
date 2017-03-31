package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDialog;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/8.
 */
public class UIContainer extends Stage {

    @FXML
    private StackPane rootStack, contentPane;
    private static StackPane rootStackS, contentPaneS;
    @FXML
    private JFXDialog dialog;
    private static JFXDialog dialogS;
    @FXML
    private Text dialogTitle, dialogContent;
    private static Text dialogTitleS, dialogContentS;
    @FXML
    private Pane loadingPane, paneCompare, paneSearch;
    private static Pane loadingPaneS;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private JFXListView<Label> searchList;
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
            rootStackS = rootStack;
            contentPaneS = contentPane;
            loadingPaneS = loadingPane;
            paneCompareListS = paneCompareList;
            dialogS = dialog;
            dialogTitleS = dialogTitle;
            dialogContentS = dialogContent;

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
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getCode() >= lower && ptr.get().getCode() <= upper).limit(10).collect(Collectors.toList());
                    } else if (first >= 'a' && first <= 'z') {
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getPinyin().startsWith(text)).limit(10).collect(Collectors.toList());
                    } else {
                        result = StockData.getIndex().stream().filter(ptr -> ptr.get().getName().contains(text)).limit(10).collect(Collectors.toList());
                    }
                    if (result != null) {
                        for (StockInfoPtr ptr : result) {
                            Label label = new Label(String.format("%06d", ptr.get().getCode()) + " " + ptr.get().getName());
                            label.setOnMouseClicked(event -> {
                                try {
                                    loadContent(new StockVC(ptr.get().getCode(), DateUtil.currentDate));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            searchList.getItems().add(label);
                        }
                    }
                }
            });
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

    public static void alert(String title, String content) {
        dialogTitleS.setText(title);
        dialogContentS.setText(content);
        dialogS.show(rootStackS);
    }

    @FXML
    private void onDialogButtonAction() {
        dialog.close();
    }

    @FXML
    private void onGlobalSearch(KeyEvent t) {
        if (t.getCode().isLetterKey() || t.getCode().isDigitKey()) {
            searchBox.appendText(t.getCharacter());
            searchBox.requestFocus();
            searchBox.positionCaret(searchBox.getLength());
        }
    }

    @FXML
    private void onSearchKeyPressed(KeyEvent t) {
        int selected = searchList.getSelectionModel().getSelectedIndex();
        switch (t.getCode()) {
            case UP:
                selected--;
                if (selected < 0) {
                    selected = 0;
                }
                t.consume();
                break;
            case DOWN:
                selected++;
                t.consume();
                break;
            case ENTER:
                if (selected < 0) {
                    selected = 0;
                }
                searchList.getSelectionModel().select(selected);
                onSearchListClicked();
                t.consume();
        }
        searchList.getSelectionModel().select(selected);
    }

    @FXML
    private void onSearchListClicked() {
        if (searchList.getSelectionModel().getSelectedItem() != null) {
            searchList.getSelectionModel().getSelectedItem().getOnMouseClicked().handle(null);
            searchBox.clear();
            paneSearch.setVisible(false);
        }
    }

    @FXML
    private void onMarketPageAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new MarketVC());
                searchBox.clear();
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
                searchBox.clear();
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
    private void onCompareAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new StockCompareVC());
                paneCompare.setVisible(false);
                searchBox.clear();
                hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onBackTestPageAction() {
        new Thread(() -> {
            try {
                showLoading();
//                loadContent(new BackTestVC());
                loadContent(new StrategyListVC());
                hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        StockVC.setIconPlusColor();
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

    static class CompareSmallWindowItemVC extends HBox {
        @FXML
        Label labelCode, labelName;
        private int code;

        public CompareSmallWindowItemVC(int code) throws IOException {
            FXUtil.loadFXML(this, getClass().getResource("assets/compareSmallWindowItem.fxml"));
            List<StockInfoPtr> list = StockData.getByCode(code);
            labelName.setText(list.get(0).get().getName());
            labelCode.setText(String.format("%06d", list.get(0).get().getCode()));
            this.code = code;
        }

        @FXML
        private void onDeleteAction() throws IOException {
            StockCompareVC.removeFromList(code);
        }
    }
}