module Compose.main {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires org.fxmisc.undo;

    opens org.lucasstarsz.composeapp.core.controllers to javafx.fxml;
    opens org.lucasstarsz.composeapp.nodes to javafx.fxml;

    exports org.lucasstarsz.composeapp.core;
}