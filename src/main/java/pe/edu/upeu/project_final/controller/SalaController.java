package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.project_final.implementacion.SalaDaoImpl;
import pe.edu.upeu.project_final.interfaces.ISalaDao;
import pe.edu.upeu.project_final.modelo.Sala;
import pe.edu.upeu.project_final.utils.UtilAlerts;

public class SalaController {
    @FXML private TextField txtNombre, txtTipo, txtCapacidad;
    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, Integer> colId, colCapacidad;
    @FXML private TableColumn<Sala, String> colNombre, colTipo;

    private final ISalaDao dao = new SalaDaoImpl();
    private ObservableList<Sala> listaSalas;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colCapacidad.setCellValueFactory(cellData -> cellData.getValue().capacidadProperty().asObject());
        listar();
    }

    @FXML
    public void guardar() {
        try {
            Sala s = new Sala();
            s.setNombre(txtNombre.getText());
            s.setTipo(txtTipo.getText());
            s.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
            if (dao.insert(s)) {
                listar();
                txtNombre.clear(); txtTipo.clear(); txtCapacidad.clear();
                UtilAlerts.mostrarAlerts("Exito", "Sala registrada con exito", "CONFIRMATION");
            } else {
                UtilAlerts.mostrarAlerts("Error", "No se pudo registrar la sala", "ERROR");
            }
        } catch (Exception e) {
            UtilAlerts.mostrarAlerts("Error", "Ingrese datos validos", "ERROR");
        }
    }

    public void listar() {
        listaSalas = FXCollections.observableArrayList(dao.listAll());
        tablaSalas.setItems(listaSalas);
    }
}
