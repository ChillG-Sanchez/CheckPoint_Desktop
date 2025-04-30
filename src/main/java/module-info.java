module org.redible.checkpoint.checkpoint {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.formdev.flatlaf;
    requires java.desktop;
    requires org.json;
    requires batik.all;
    requires okhttp3;

    opens org.redible.checkpoint.checkpoint to javafx.fxml;
    exports org.redible.checkpoint.checkpoint;
    exports org.redible.checkpoint.checkpoint.ui;
    opens org.redible.checkpoint.checkpoint.ui to javafx.fxml;
    exports org.redible.checkpoint.checkpoint.ui.scanner;
    opens org.redible.checkpoint.checkpoint.ui.scanner to javafx.fxml;
}