package nci.security_fundamentals;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML private TabPane mainTabPane;
    @FXML private Tab pwTab;
    @FXML private Tab imgTab;
    @FXML private Tab msgTab;
    @FXML private Tab homeTab;

    @FXML
    private void openPwTab() {
        mainTabPane.getSelectionModel().select(pwTab);
    }

    @FXML
    private void openImgTab() {
        mainTabPane.getSelectionModel().select(imgTab);
    }

    @FXML
    private void openMsgTab() {
        mainTabPane.getSelectionModel().select(msgTab);
    }

    @FXML
    private void openHomeTab(){
        mainTabPane.getSelectionModel().select(homeTab);
    }
}
