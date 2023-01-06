module Photos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens photos.model;
    opens photos.view;
    exports photos.model;
    exports photos.view;
}