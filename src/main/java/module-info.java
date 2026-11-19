module pe.edu.upeu.taller_colaborativo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.base;
    requires javafx.graphics;

    opens pe.edu.upeu.taller_colaborativo to javafx.fxml;

    opens pe.edu.upeu.taller_colaborativo.modelo to javafx.base, javafx.fxml;
    opens pe.edu.upeu.taller_colaborativo.controller to javafx.fxml;

    exports pe.edu.upeu.taller_colaborativo;

    exports pe.edu.upeu.taller_colaborativo.utils;
    exports pe.edu.upeu.taller_colaborativo.controller;
}
