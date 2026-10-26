package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.project_final.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IPeliculaDao;
import pe.edu.upeu.project_final.modelo.Pelicula;
import pe.edu.upeu.project_final.utils.UtilAlerts;

public class PeliculaController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtGenero;
    @FXML
    private TextField txtDuracion;
    @FXML
    private TextField txtClasificacion;

    @FXML
    private TableView<Pelicula> tablaPeliculas;
    @FXML
    private TableColumn<Pelicula, Integer> colId;
    @FXML
    private TableColumn<Pelicula, String> colTitulo;
    @FXML
    private TableColumn<Pelicula, String> colGenero;
    @FXML
    private TableColumn<Pelicula, Integer> colDuracion;
    @FXML
    private TableColumn<Pelicula, String> colClasificacion;

    private final IPeliculaDao dao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> listaPeliculas;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colTitulo.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        colGenero.setCellValueFactory(cellData -> cellData.getValue().generoProperty());
        colDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionMinProperty().asObject());
        colClasificacion.setCellValueFactory(cellData -> cellData.getValue().clasificacionProperty());
        listar();
    }

    @FXML
    public void guardar() {
        try {
            Pelicula p = new Pelicula();
            p.setTitulo(txtTitulo.getText());
            p.setGenero(txtGenero.getText());
            p.setDuracionMin(Integer.parseInt(txtDuracion.getText()));
            p.setClasificacion(txtClasificacion.getText());
            if (dao.insert(p)) {
                listar();
                txtTitulo.clear();
                txtGenero.clear();
                txtDuracion.clear();
                txtClasificacion.clear();
                UtilAlerts.mostrarAlerts("Exito", "Pelicula registrada con exito", "CONFIRMATION");
            } else {
                UtilAlerts.mostrarAlerts("Error", "No se pudo registrar la pelicula", "ERROR");
            }
        } catch (Exception e) {
            UtilAlerts.mostrarAlerts("Error", "Ingrese datos validos", "ERROR");
        }
    }

    public void listar() {
        listaPeliculas = FXCollections.observableArrayList(dao.listAll());
        tablaPeliculas.setItems(listaPeliculas);
    }
}
