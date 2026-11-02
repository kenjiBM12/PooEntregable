module pe.edu.upeu.taller_colaborativo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens pe.edu.upeu.taller_colaborativo to javafx.fxml;
    exports pe.edu.upeu.taller_colaborativo;
}