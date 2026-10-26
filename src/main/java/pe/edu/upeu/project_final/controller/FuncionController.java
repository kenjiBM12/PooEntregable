package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.project_final.implementacion.FuncionDaoImpl;
import pe.edu.upeu.project_final.interfaces.IFuncionDao;
import pe.edu.upeu.project_final.modelo.Funcion;
import pe.edu.upeu.project_final.utils.UtilAlerts;

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

    private final IFuncionDao dao = new FuncionDaoImpl();
    private ObservableList<Funcion> listaFunciones;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colPelicula.setCellValueFactory(cellData -> cellData.getValue().idPeliculaProperty().asObject());
        colSala.setCellValueFactory(cellData -> cellData.getValue().idSalaProperty().asObject());
        colHorario.setCellValueFactory(cellData -> cellData.getValue().horarioProperty());
        colPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
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
            if (dao.insert(f)) {
                listar();
                txtIdPelicula.clear();
                txtIdSala.clear();
                txtHorario.clear();
                txtPrecio.clear();
                UtilAlerts.mostrarAlerts("Exito", "Funcion registrada con exito", "CONFIRMATION");
            } else {
                UtilAlerts.mostrarAlerts("Error", "No se pudo registrar la funcion", "ERROR");
            }
        } catch (Exception e) {
            UtilAlerts.mostrarAlerts("Error", "Verifique que los IDs y el precio sean numericos", "ERROR");
        }
    }

    public void listar() {
        listaFunciones = FXCollections.observableArrayList(dao.listAll());
        tablaFunciones.setItems(listaFunciones);
    }
}
