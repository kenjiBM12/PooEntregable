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
import pe.edu.upeu.taller_colaborativo.modelo.Pelicula;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.util.Optional;

public class PeliculaController {

    @FXML
    private TableView<Pelicula> tablePelicula;
    @FXML
    private TableColumn<Pelicula, Number> colNumero;
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
    @FXML
    private TableColumn<Pelicula, String> colEstado;

    // Es static para que la lista se conserve mientras la aplicacion este abierta.
    // Cuando el equipo conecte la base de datos esto lo reemplaza peliculaDao.listAll()
    private static final ObservableList<Pelicula> listaPeliculas = FXCollections.observableArrayList(
            new Pelicula(1, "Dune: Parte Dos", "Ciencia ficción", 166, "+14", "Activa", "dune.png"),
            new Pelicula(2, "Intensamente 2", "Animación", 96, "TP", "Activa", "intensamente.png"),
            new Pelicula(3, "Deadpool & Wolverine", "Acción", 128, "+16", "Activa", "deadpool.png"),
            new Pelicula(4, "Garfield: Fuera de casa", "Comedia", 101, "TP", "Activa", "garfield.png"),
            new Pelicula(5, "El Planeta de los Simios: Nuevo Reino", "Aventura", 145, "+14", "Activa", "simios.png"),
            new Pelicula(6, "Un Lugar en Silencio: Día Uno", "Terror", 100, "+16", "Inactiva", "silencio.png"));

    public static ObservableList<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    @FXML
    public void initialize() {
        // Columna correlativa que se numera sola
        colNumero.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio ? null : String.valueOf(getIndex() + 1));
            }
        });

        colId.setCellValueFactory(dato -> dato.getValue().idPeliculaProperty().asObject());
        colTitulo.setCellValueFactory(dato -> dato.getValue().tituloProperty());
        colGenero.setCellValueFactory(dato -> dato.getValue().generoProperty());
        colDuracion.setCellValueFactory(dato -> dato.getValue().duracionProperty().asObject());
        colClasificacion.setCellValueFactory(dato -> dato.getValue().clasificacionProperty());
        colEstado.setCellValueFactory(dato -> dato.getValue().estadoProperty());

        // La duracion se muestra en minutos
        colDuracion.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null : item + " min");
            }
        });

        // El estado se pinta con color, igual que las reservas del profe
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacio) {
                super.updateItem(estado, vacio);
                setText(vacio ? null : estado);
                setStyle(estado == null ? ""
                        : estado.equals("Activa")
                        ? "-fx-text-fill: #6FCF97; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E4766C; -fx-font-weight: bold;");
            }
        });

        tablePelicula.setItems(listaPeliculas);
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (avisarSiNoHaySeleccion("Seleccione una película de la tabla para editarla.")) {
            showForm(tablePelicula.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void handleDelete() {
        if (avisarSiNoHaySeleccion("Seleccione una película de la tabla para eliminarla.")) {
            Pelicula seleccionada = tablePelicula.getSelectionModel().getSelectedItem();

            Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                    "¿Está seguro de eliminar la película " + seleccionada.getTitulo() + "?",
                    "CONFIRMATION");
            Optional<ButtonType> respuesta = confirmacion.showAndWait();

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                listaPeliculas.remove(seleccionada);
            }
        }
    }

    // Devuelve true cuando hay una fila seleccionada
    private boolean avisarSiNoHaySeleccion(String mensaje) {
        if (tablePelicula.getSelectionModel().getSelectedItem() == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", mensaje, "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    // Si la pelicula viene null el formulario abre vacio (registro nuevo)
    private void showForm(Pelicula pelicula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/pelicula-form.fxml"));
            Parent root = loader.load();

            PeliculaFormController controlador = loader.getController();
            controlador.setPelicula(pelicula);

            Stage stage = new Stage();
            stage.setTitle(pelicula == null ? "Nueva película" : "Editar película");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controlador.isGuardado()) {
                if (pelicula == null) {
                    Pelicula nueva = controlador.getPelicula();
                    nueva.setIdPelicula(siguienteId());
                    listaPeliculas.add(nueva);
                } else {
                    // Los datos se guardaron sobre el mismo objeto, solo se refresca la tabla
                    tablePelicula.refresh();
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el formulario de película: " + e.getMessage());
        }
    }

    private int siguienteId() {
        return listaPeliculas.stream().mapToInt(Pelicula::getIdPelicula).max().orElse(0) + 1;
    }
}
