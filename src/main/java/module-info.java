module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;
    requires jbcrypt;

    opens org.example.frontend to javafx.fxml;
    exports org.example.frontend;
}