module pe.edu.upeu.gestioo_de_cine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;



    opens pe.edu.upeu.gestioo_de_cine to javafx.fxml;
    opens pe.edu.upeu.gestioo_de_cine.controller to javafx.fxml;
    exports pe.edu.upeu.gestioo_de_cine;
    opens pe.edu.upeu.gestioo_de_cine.implementacion to javafx.fxml;
}