package com.example.proyecto_final.controller;

import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalaController {
    @FXML private TextField txtNombre, txtTipo, txtCapacidad;
    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, Integer> colId, colCapacidad;
    @FXML private TableColumn<Sala, String> colNombre, colTipo;

    private ISalaDao dao = new SalaDaoImpl();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        listar();
    }

    @FXML
    public void guardar() {
        try {
            Sala s = new Sala();
            s.setNombre(txtNombre.getText());
            s.setTipo(txtTipo.getText());
            s.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
            dao.insertar(s);
            listar();
            txtNombre.clear(); txtTipo.clear(); txtCapacidad.clear();
            UtilAlerts.mostrarAlerta("Éxito", "Sala registrada con éxito", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            UtilAlerts.mostrarAlerta("Error", "Ingrese datos válidos", Alert.AlertType.ERROR);
        }
    }

    public void listar() {
        ObservableList<Sala> data = FXCollections.observableArrayList(dao.listar());
        tablaSalas.setItems(data);
    }
}