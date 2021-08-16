module smlego {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires java.base;
    requires java.desktop;
    requires java.logging;

    //requires javafx.graphicsEmpty;
    opens smlego to javafx.fxml;
    exports smlego;
}