package pe.edu.upeu.taller_colaborativo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.modelo.Sala;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.util.Optional;

public class SalaController {

    @FXML
    private TableView<Sala> tableSala;
    @FXML
    private TableColumn<Sala, Number> colNumero;
    @FXML
    private TableColumn<Sala, Integer> colId;
    @FXML
    private TableColumn<Sala, String> colNombre;
    @FXML
    private TableColumn<Sala, Integer> colFilas;
    @FXML
    private TableColumn<Sala, Integer> colColumnas;
    @FXML
    private TableColumn<Sala, String> colTipo;
    @FXML
    private TableColumn<Sala, String> colEstado;

    // Es static para que la lista se conserve mientras la aplicacion este abierta
    private static final ObservableList<Sala> listaSalas = FXCollections.observableArrayList(
            new Sala(1, "Sala 1", 10, 12, "2D", "Disponible"),
            new Sala(2, "Sala 2", 10, 12, "3D", "Disponible"),
            new Sala(3, "Sala 3", 8, 10, "2D", "Disponible"),
            new Sala(4, "Sala 4", 12, 14, "IMAX", "Mantenimiento"));

    public static ObservableList<Sala> getListaSalas() {
        return listaSalas;
    }

    @FXML
    public void initialize() {
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio ? null : String.valueOf(getIndex() + 1));
            }
        });

        colId.setCellValueFactory(dato -> dato.getValue().idSalaProperty().asObject());
        colNombre.setCellValueFactory(dato -> dato.getValue().nombreProperty());
        colFilas.setCellValueFactory(dato -> dato.getValue().filasProperty().asObject());
        colColumnas.setCellValueFactory(dato -> dato.getValue().columnasProperty().asObject());
        colTipo.setCellValueFactory(dato -> dato.getValue().tipoProperty());
        colEstado.setCellValueFactory(dato -> dato.getValue().estadoProperty());

        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacio) {
                super.updateItem(estado, vacio);
                setText(vacio ? null : estado);
                setStyle(estado == null ? ""
                        : estado.equals("Disponible")
                        ? "-fx-text-fill: #6FCF97; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E4766C; -fx-font-weight: bold;");
            }
        });

        tableSala.setItems(listaSalas);
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (avisarSiNoHaySeleccion("Seleccione una sala de la tabla para editarla.")) {
            showForm(tableSala.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void handleDelete() {
        if (avisarSiNoHaySeleccion("Seleccione una sala de la tabla para eliminarla.")) {
            Sala seleccionada = tableSala.getSelectionModel().getSelectedItem();

            Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                    "¿Está seguro de eliminar la " + seleccionada.getNombre() + "?",
                    Alert.AlertType.CONFIRMATION);
            Optional<ButtonType> respuesta = confirmacion.showAndWait();

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                listaSalas.remove(seleccionada);
            }
        }
    }

    private boolean avisarSiNoHaySeleccion(String mensaje) {
        if (tableSala.getSelectionModel().getSelectedItem() == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", mensaje, Alert.AlertType.WARNING).showAndWait();
            return false;
        }
        return true;
    }

    private void showForm(Sala sala) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/sala-form.fxml"));
            Parent root = loader.load();

            SalaFormController controlador = loader.getController();
            controlador.setSala(sala);

            Stage stage = new Stage();
            stage.setTitle(sala == null ? "Nueva sala" : "Editar sala");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controlador.isGuardado()) {
                if (sala == null) {
                    Sala nueva = controlador.getSala();
                    nueva.setIdSala(siguienteId());
                    listaSalas.add(nueva);
                } else {
                    tableSala.refresh();
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el formulario de sala: " + e.getMessage());
        }
    }

    private int siguienteId() {
        return listaSalas.stream().mapToInt(Sala::getIdSala).max().orElse(0) + 1;
    }
}
