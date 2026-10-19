package com.example.proyecto_final.controller;

import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FuncionController {
    @FXML
    private TextField txtIdPelicula, txtIdSala, txtHorario, txtPrecio;
    @FXML
    private TableView<Funcion> tablaFunciones;
    @FXML
    private TableColumn<Funcion, Integer> colId, colPelicula, colSala;
    @FXML
    private TableColumn<Funcion, String> colHorario;
    @FXML
    private TableColumn<Funcion, Double> colPrecio;

    private IFuncionDao dao = new FuncionDaoImpl();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("idPelicula"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        listar();
    }

    @FXML
    public void guardar() {
        try {
            Funcion f = new Funcion();
            f.setIdPelicula(Integer.parseInt(txtIdPelicula.getText()));
            f.setIdSala(Integer.parseInt(txtIdSala.getText()));
            f.setHorario(txtHorario.getText());
            f.setPrecio(Double.parseDouble(txtPrecio.getText()));
            dao.insertar(f);
            listar();
            txtIdPelicula.clear();
            txtIdSala.clear();
            txtHorario.clear();
            txtPrecio.clear();
            UtilAlerts.mostrarAlerta("Éxito", "Función registrada correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            UtilAlerts.mostrarAlerta("Error", "Verifique que los IDs y el precio sean numéricos", Alert.AlertType.ERROR);
        }
    }

    public void listar() {
        ObservableList<Funcion> data = FXCollections.observableArrayList(dao.listar());
        tablaFunciones.setItems(data);
    }
}