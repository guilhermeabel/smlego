module smlego {
    requires java.base;
    requires transitive java.desktop;
    requires java.logging;

    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires transitive javafx.graphics;

    opens smlego to javafx.fxml;

    exports smlego;
}
