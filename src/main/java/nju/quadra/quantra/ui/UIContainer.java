package nju.quadra.quantra.ui;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.HashSet;
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
    private Pane loadingPane, paneCompare, paneSearch;
    private static Pane loadingPaneS;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private JFXListView<Label> searchList;
    @FXML
    private VBox paneCompareList;
    private static VBox paneCompareListS;
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
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Text(title));
        layout.setBody(new Text(content));
        JFXButton button = new JFXButton("确定");
        layout.setActions(button);
        JFXDialog dialog = new JFXDialog(rootStackS, layout, JFXDialog.DialogTransition.CENTER);
        button.setOnAction(t -> dialog.close());
        dialog.show();
    }

    public static void confirm(String title, String content, EventHandler<ActionEvent> onYes) {
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Text(title));
        layout.setBody(new Text(content));
        JFXButton yesButton = new JFXButton("确定");
        JFXButton noButton = new JFXButton("取消");
        layout.setActions(yesButton, noButton);
        JFXDialog dialog = new JFXDialog(rootStackS, layout, JFXDialog.DialogTransition.CENTER);
        yesButton.setOnAction(t -> {
            onYes.handle(t);
            dialog.close();
        });
        noButton.setOnAction(t -> dialog.close());
        dialog.show();
    }

    public static void addStockToPool(int code) {
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Text("请选择目标股票池 :"));
        JFXListView<Node> list = new JFXListView();
        layout.setBody(list);
        JFXButton noButton = new JFXButton("取消");
        layout.setActions(noButton);
        JFXDialog dialog = new JFXDialog(rootStackS, layout, JFXDialog.DialogTransition.CENTER);
        noButton.setOnAction(t -> dialog.close());
        list.getItems().addAll(StockPoolData.getPoolMap().values().stream()
                .map(pool -> {
                    Label label = new Label(pool.name);
                    MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FORMAT_LIST_BULLETED);
                    icon.setSize("20px");
                    label.setGraphic(icon);
                    label.setUserData(pool);
                    return label;
                }).collect(Collectors.toList()));
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            CustomPool pool = (CustomPool) newValue.getUserData();
            HashSet<Integer> set = new HashSet<>(pool.getStockPool());
            if (set.add(code)) {
                CustomPool newPool = new CustomPool(pool.name, set);
                StockPoolData.removePool(pool);
                StockPoolData.addPool(newPool);
                UIContainer.alert("提示", "股票添加成功");
            } else {
                UIContainer.alert("提示", "该股票已在所选股池中");
            }
            dialog.close();
        });
        dialog.show();
    }

    @FXML
    private void onGlobalSearch(KeyEvent t) {
        if ((!t.isControlDown() && !t.isMetaDown() && !t.isAltDown() && !t.isShiftDown() && !t.isShortcutDown())
                && (t.getCode().isLetterKey() || t.getCode().isDigitKey())) {
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
    private void onComparePageAction() throws IOException {
        loadCompareList();
        paneCompare.setVisible(true);
    }


    @FXML
    private void onStockPoolAction() {
        new Thread(() -> {
            try {
                showLoading();
                loadContent(new PoolListVC());
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
    private void onBackTestPageAction() {
        new Thread(() -> {
            try {
                showLoading();
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