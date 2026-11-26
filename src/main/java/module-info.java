module pe.edu.upeu.gestioo_de_cine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;

    opens pe.edu.upeu.gestioo_de_cine to javafx.fxml;

    opens pe.edu.upeu.gestioo_de_cine.modelo to javafx.base, javafx.fxml;
    opens pe.edu.upeu.gestioo_de_cine.controller to javafx.fxml;
    opens pe.edu.upeu.gestioo_de_cine.views to javafx.fxml;

    exports pe.edu.upeu.gestioo_de_cine;

    exports pe.edu.upeu.gestioo_de_cine.utils;
    exports pe.edu.upeu.gestioo_de_cine.test;
    exports pe.edu.upeu.gestioo_de_cine.interfaces;
    exports pe.edu.upeu.gestioo_de_cine.implementacion;
    exports pe.edu.upeu.gestioo_de_cine.controller;
}
