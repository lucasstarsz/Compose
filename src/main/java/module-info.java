module Compose.main {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.fxmisc.richtext;
    requires reactfx;
    requires flowless;

    opens org.lucasstarsz.composeapp.core.controllers to javafx.fxml;
    opens org.lucasstarsz.composeapp.nodes to javafx.fxml;

    exports org.lucasstarsz.composeapp.core;
    exports org.lucasstarsz.composeapp.core.controllers;
    exports org.lucasstarsz.composeapp.nodes;
    exports org.lucasstarsz.composeapp.user;
}