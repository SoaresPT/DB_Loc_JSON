module org.example.db_gui_ex {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens org.example.db_gui_ex to javafx.fxml, com.google.gson;
    exports org.example.db_gui_ex;
}