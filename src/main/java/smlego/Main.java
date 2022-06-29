package smlego;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Main extends Application {

    private static Scene scene;
    private double xOffset;
    private double yOffset;
    private String themeLight = getClass().getResource("themeLight/style.css").toExternalForm();
    //private String themeYellow = getClass().getResource("themeYellow/style.css").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.TRANSPARENT);
        scene = new Scene(loadFXML("primary.fxml"), 800, 600);
        scene.getStylesheets().add(themeLight);
        stage.setScene(scene);
        stage.show();

        scene.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
        Parent root = loader.load();
        return root;
    }

    // public void setStyle(String style){
    //     if (style == "primary"){
    //         scene.getStylesheets().remove(this.themeLight);
    //         if(!scene.getStylesheets().contains(this.themeYellow)) scene.getStylesheets().add(this.themeYellow);
    //     } else if (style == "secondary"){
    //         scene.getStylesheets().remove(this.themeYellow);
    //         if(!scene.getStylesheets().contains(this.themeLight)) scene.getStylesheets().add(this.themeLight);
    //     }
    // }

    public static void main(String[] args) {
        launch();
    }
}