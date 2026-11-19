package pe.edu.upeu.taller_colaborativo.controller;

import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;
import pe.edu.upeu.taller_colaborativo.modelo.Pelicula;
import pe.edu.upeu.taller_colaborativo.modelo.Sala;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class FuncionController {

    @FXML
    private TableView<Funcion> tableFuncion;
    @FXML
    private TableColumn<Funcion, Number> colNumero;
    @FXML
    private TableColumn<Funcion, Integer> colId;
    @FXML
    private TableColumn<Funcion, Pelicula> colPelicula;
    @FXML
    private TableColumn<Funcion, Sala> colSala;
    @FXML
    private TableColumn<Funcion, LocalDate> colFecha;
    @FXML
    private TableColumn<Funcion, String> colHora;
    @FXML
    private TableColumn<Funcion, Double> colPrecio;
    @FXML
    private TableColumn<Funcion, Number> colOcupacion;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Es static para que la lista se conserve mientras la aplicacion este abierta
    private static final ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList();

    // Los ejemplos se cargan al usar la clase, asi la venta de entradas ya tiene funciones
    // aunque el usuario no haya abierto antes la vista de Funciones
    static {
        cargarEjemplos();
    }

    public static ObservableList<Funcion> getListaFunciones() {
        return listaFunciones;
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

        colId.setCellValueFactory(dato -> dato.getValue().idFuncionProperty().asObject());
        colPelicula.setCellValueFactory(dato -> dato.getValue().peliculaProperty());
        colSala.setCellValueFactory(dato -> dato.getValue().salaProperty());
        colFecha.setCellValueFactory(dato -> dato.getValue().fechaProperty());
        colHora.setCellValueFactory(dato -> dato.getValue().horaProperty());
        colPrecio.setCellValueFactory(dato -> dato.getValue().precioProperty().asObject());

        // La columna ordena por butacas vendidas y se muestra como "vendidas / aforo"
        colOcupacion.setCellValueFactory(dato -> new SimpleIntegerProperty(
                VentaListaController.butacasVendidasDe(dato.getValue()).size()));

        colOcupacion.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Number vendidas, boolean vacio) {
                super.updateItem(vendidas, vacio);
                TableRow<Funcion> fila = getTableRow();

                setText(vacio || fila == null || fila.getItem() == null ? null
                        : vendidas + " / " + fila.getItem().getSala().getAforo());
            }
        });

        // La fecha se muestra como dd/MM/yyyy y el precio con dos decimales
        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setText(vacio || fecha == null ? null : fecha.format(FORMATO_FECHA));
            }
        });

        colPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean vacio) {
                super.updateItem(precio, vacio);
                setText(vacio || precio == null ? null : String.format(Locale.US, "S/ %.2f", precio));
            }
        });

        tableFuncion.setItems(listaFunciones);
    }

    // Funciones de ejemplo, armadas con las peliculas y salas que ya existen
    private static void cargarEjemplos() {
        List<Pelicula> peliculas = PeliculaController.getListaPeliculas();
        List<Sala> salas = SalaController.getListaSalas();

        if (!listaFunciones.isEmpty() || peliculas.isEmpty() || salas.isEmpty()) {
            return;
        }

        listaFunciones.add(new Funcion(1, elegir(peliculas, 0), elegir(salas, 2),
                LocalDate.now(), "21:00", 18.00));
        listaFunciones.add(new Funcion(2, elegir(peliculas, 1), elegir(salas, 0),
                LocalDate.now(), "15:30", 16.00));
        listaFunciones.add(new Funcion(3, elegir(peliculas, 2), elegir(salas, 1),
                LocalDate.now(), "19:20", 22.00));
        listaFunciones.add(new Funcion(4, elegir(peliculas, 0), elegir(salas, 0),
                LocalDate.now().plusDays(1), "17:45", 18.00));
    }

    // Toma un elemento de la lista sin salirse del rango
    private static <T> T elegir(List<T> lista, int posicion) {
        return lista.get(posicion % lista.size());
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (avisarSiNoHaySeleccion("Seleccione una función de la tabla para editarla.")) {
            showForm(tableFuncion.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void handleDelete() {
        if (avisarSiNoHaySeleccion("Seleccione una función de la tabla para eliminarla.")) {
            Funcion seleccionada = tableFuncion.getSelectionModel().getSelectedItem();

            int ventas = VentaListaController.ventasDe(seleccionada);
            Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                    ventas == 0
                            ? "¿Está seguro de eliminar esta función?"
                            : "¿Está seguro de eliminar esta función? También se borrarán sus "
                            + ventas + (ventas == 1 ? " venta registrada." : " ventas registradas."),
                    "CONFIRMATION");
            Optional<ButtonType> respuesta = confirmacion.showAndWait();

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                // Tambien se borran las ventas de esa funcion
                VentaListaController.eliminarPorFuncion(seleccionada);
                listaFunciones.remove(seleccionada);
            }
        }
    }

    private boolean avisarSiNoHaySeleccion(String mensaje) {
        if (tableFuncion.getSelectionModel().getSelectedItem() == null) {
            UtilAlerts.mostrarAlerts("Selección requerida", mensaje, "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    private void showForm(Funcion funcion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/funcion-form.fxml"));
            Parent root = loader.load();

            FuncionFormController controlador = loader.getController();
            controlador.setFuncion(funcion);

            Stage stage = new Stage();
            stage.setTitle(funcion == null ? "Nueva función" : "Editar función");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controlador.isGuardado()) {
                if (funcion == null) {
                    Funcion nueva = controlador.getFuncion();
                    nueva.setIdFuncion(siguienteId());
                    listaFunciones.add(nueva);
                } else {
                    tableFuncion.refresh();
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el formulario de función: " + e.getMessage());
        }
    }

    private int siguienteId() {
        return listaFunciones.stream().mapToInt(Funcion::getIdFuncion).max().orElse(0) + 1;
    }
}
