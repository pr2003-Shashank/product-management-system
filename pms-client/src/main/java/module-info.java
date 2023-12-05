module com.example.pmsclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.logging;

    opens com.example.pmsclient to javafx.fxml;
    exports com.example.pmsclient;
}