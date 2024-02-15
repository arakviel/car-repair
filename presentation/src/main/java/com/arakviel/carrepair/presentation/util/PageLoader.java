package com.arakviel.carrepair.presentation.util;

import com.arakviel.carrepair.presentation.Main;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public final class PageLoader {

    private static final Map<String, MyController> controllers = new HashMap<>();
    public static Stage primaryStage;

    public static MyController load(Pane contentPane, String directory, String fxmlName) {
        String key = "%s.%s".formatted(directory, fxmlName);
        Parent root = null;

        MyController myController = controllers.get(key);
        if (Objects.isNull(myController)) {
            FXMLLoader fxmlLoader = null;
            try {
                fxmlLoader = new FXMLLoader(
                        Main.class.getResource("view/%s/%s.fxml".formatted(directory, fxmlName)));
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            myController = new MyController(fxmlLoader.getController(), root, contentPane);
            controllers.put(key, myController);
        } else {
            root = myController.root();
        }

        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(root);

        return myController;
    }

    public static MyController getController(String fxmlName) {
        return controllers.get(fxmlName);
    }

    public static void setInnerContent(MyController myController) {
        var anchorPane = myController.contentPane();
        anchorPane.getChildren().removeAll();
        anchorPane.getChildren().setAll(myController.root());
    }

    private PageLoader() {}
}
