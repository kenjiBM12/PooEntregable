// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

/**
 * CONTROLADOR DE TABLA: configura columnas y carga los registros del DAO.
 * Nuevo/Editar abren un formulario modal; Eliminar pide confirmación.
 * Sigue el patrón de ClienteController del hotel.
 */
public class PeliculaController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    // Se programa contra la interfaz; DaoImpl es la implementación concreta que ejecuta el SQL.
    private final IPeliculaDao dao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> lista;

    // La tabla del FXML se inyecta aquí: su fx:id debe coincidir con este nombre.
    @FXML private TableView<Pelicula> tabla;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, Number> colNumero;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, Integer> colId;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, String> colTitulo;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, String> colGenero;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    // Columna tipada: el primer tipo es el modelo de la fila; el segundo es el dato mostrado.
    @FXML private TableColumn<Pelicula, String> colClasificacion;

    @FXML
    /**
     * Se ejecuta automáticamente después de cargar e inyectar el FXML.
     * Configura las columnas o controles y carga los primeros datos.
     */
    public void initialize() {
        colNumero.setCellFactory(column -> new TableCell<>() {
            @Override
            /**
             * JavaFX reutiliza celdas y llama aquí al cambiar su contenido.
             * Se limpia el texto en filas vacías para no mostrar datos de otra fila.
             */
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        // La expresión lambda toma una fila y devuelve la propiedad que esa columna debe mostrar.
        colId.setCellValueFactory(cellData ->
                // asObject() adapta la propiedad numérica a Integer/Double para el tipo de TableColumn.
                cellData.getValue().idpeliculaProperty().asObject());
        colTitulo.setCellValueFactory(cellData ->
                cellData.getValue().tituloProperty());
        colGenero.setCellValueFactory(cellData ->
                cellData.getValue().generoProperty());
        colDuracion.setCellValueFactory(cellData ->
                cellData.getValue().duracionProperty().asObject());
        colClasificacion.setCellValueFactory(cellData ->
                cellData.getValue().clasificacionProperty());
        cargarDatos();
    }

    /**
     * Consulta el DAO y actualiza los elementos visibles de la tabla o reporte.
     * Se llama al iniciar y al terminar las operaciones que cambian los datos.
     */
    private void cargarDatos() {
        // ObservableList permite que TableView observe la colección obtenida del DAO.
        lista = FXCollections.observableArrayList(dao.listAll());
        tabla.setItems(lista);
    }

    @FXML
    /**
     * Atiende el botón Nuevo: null indica que aún no hay un registro para editar.
     */
    private void handleNew() {
        showForm(null);
    }

    @FXML
    /**
     * Obtiene la fila seleccionada, busca el registro completo y abre el formulario.
     * Si no hay selección o no se recupera el registro, muestra un mensaje.
     */
    private void handleEdit() {
        // Obtiene el objeto de la fila seleccionada; si no hay selección devuelve null.
        Pelicula seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para editar", "WARNING").showAndWait();
            return;
        }
        Pelicula completo = dao.searchById(seleccionado.getIdpelicula());
        if (completo == null) {
            utilAlerts.mostrarAlerts("Error", "No se pudo recuperar el registro", "ERROR").showAndWait();
            return;
        }
        showForm(completo);
    }

    @FXML
    /**
     * Atiende Eliminar: exige selección y confirmación antes de llamar al DAO.
     */
    private void handleDelete() {
        Pelicula seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para eliminar", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Eliminar el registro " + seleccionado.getIdpelicula() + "?", "CONFIRMATION");
        // Optional representa que puede existir una respuesta o que el diálogo se cerró sin ella.
        Optional<ButtonType> respuesta = confirm.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            if (dao.delete(seleccionado)) {
                cargarDatos();
            } else {
                utilAlerts.mostrarAlerts("Error",
                        "No se pudo eliminar. Revise las relaciones y la consola", "ERROR").showAndWait();
            }
        }
    }

    /**
     * Carga el FXML del formulario y le pasa el objeto a su controlador.
     * showAndWait espera a que se cierre la ventana modal.
     * Si isSaved es true, inserta o actualiza y vuelve a cargar la tabla.
     */
    private void showForm(Pelicula registro) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/com/example/proyecto_final/views/pelicula-form.fxml"));
            Parent root = loader.load();
            PeliculaFormController controller = loader.getController();
            controller.setPelicula(registro);

            Stage stage = new Stage();
            stage.setTitle(registro == null ? "Nuevo registro" : "Editar registro");
            stage.initOwner(tabla.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            // La ventana modal debe cerrarse antes de continuar con la decisión de guardar.
            stage.showAndWait();

            // Solo se escribe en la base cuando el formulario confirmó Guardar.
            if (controller.isSaved()) {
                Pelicula procesado = controller.getPelicula();
                boolean resultado = registro == null ? dao.insert(procesado) : dao.update(procesado);
                utilAlerts.mostrarAlerts(resultado ? "Registro" : "Error",
                        resultado ? "Operación realizada correctamente" : "No se pudo guardar. Revise la consola",
                        resultado ? "INFORMATION" : "ERROR").showAndWait();
                cargarDatos();
            }
        } catch (IOException e) {
            e.printStackTrace();
            utilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario", "ERROR").showAndWait();
        }
    }
}
