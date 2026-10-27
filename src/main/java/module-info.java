
module pe.edu.upeu.project_final {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    //requiero la dependencia de sqlite
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;

    opens pe.edu.upeu.project_final to javafx.fxml;

    opens pe.edu.upeu.project_final.modelo to javafx.base, javafx.fxml;
    opens pe.edu.upeu.project_final.controller to javafx.fxml;

    exports pe.edu.upeu.project_final;

    exports pe.edu.upeu.project_final.utils;
    exports pe.edu.upeu.project_final.test;
    exports pe.edu.upeu.project_final.interfaces;
    exports pe.edu.upeu.project_final.implementacion;
    exports pe.edu.upeu.project_final.controller;

}