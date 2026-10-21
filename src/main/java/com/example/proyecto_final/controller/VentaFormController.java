// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.proyecto_final.implementacion.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

/**
 * CONTROLADOR DEL FORMULARIO: recibe datos nuevos o los datos que se van a editar.
 * Valida campos y prepara el objeto; isSaved indica si el usuario pulsó Guardar.
 * El controlador de tabla realiza insert/update al cerrarse esta ventana modal.
 */
public class VentaFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Venta registro;
    // Estado del formulario: empieza en false y solo pasa a true después de Guardar válido.
    private boolean saved = false;
    // Texto visible de la vista que este controlador puede cambiar.
    @FXML private Label lblTitulo;
    // Lista desplegable de objetos: getValue() devuelve el objeto seleccionado.
    @FXML private ComboBox<Funcion> cbFuncion;
    // Campo de texto del formulario: getText() devuelve String, incluso si se escribe un número.
    @FXML private TextField txtCliente, txtCantidad;

    @FXML
    /**
     * FXMLLoader llama a initialize después de inyectar los controles @FXML.
     * En formularios con combos se cargan sus opciones; si está vacío no hay preparación adicional.
     */
    public void initialize() {
        cbFuncion.setItems(FXCollections.observableArrayList(new FuncionDaoImpl().listAll()));
        cbFuncion.setConverter(new StringConverter<>() {
            /**
             * Define el texto que el ComboBox muestra para un objeto.
             * El valor seleccionado sigue siendo el objeto, no este texto.
             */
            public String toString(Funcion f) {
                return f == null ? "" : f.getIdfuncion() + " - " + f.getPelicula().getTitulo()
                        + " | " + f.getSala().getNombre() + " | " + f.getHorario()
                        + " | S/ " + f.getPrecio();
            }
            /**
             * Conversión inversa requerida por StringConverter.
             * Los combos no son editables; aquí no se interpreta texto escrito a mano.
             */
            public Funcion fromString(String text) { return null; }
        });
    }

    /**
     * Permite al controlador de tabla saber si el usuario confirmó Guardar.
     * Cancelar o cerrar sin guardar mantiene este valor en false.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Entrega al controlador de tabla el objeto preparado por este formulario.
     */
    public Venta getVenta() {
        return registro;
    }

    /**
     * Recibe el objeto que se editará; null significa registro nuevo.
     * Carga sus datos en los controles y reinicia saved=false.
     */
    public void setVenta(Venta value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            cbFuncion.getItems().stream()
                    .filter(f -> f.getIdfuncion() == value.getFuncion().getIdfuncion())
                    .findFirst().ifPresent(f -> cbFuncion.setValue(f));
            txtCliente.setText(value.getCliente());
            txtCantidad.setText(String.valueOf(value.getCantidadAsientos()));
        }
    }

    /**
     * Comprueba campos obligatorios y valores numéricos antes de aceptar el formulario.
     * Devuelve false y muestra un mensaje si algún dato no cumple la validación.
     */
    private boolean validate() {
        if (cbFuncion.getValue() == null || txtCliente.getText().isBlank()) {
            return error("Seleccione función e ingrese el cliente");
        }
        try {
            // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = cbFuncion.getValue().getPrecio();
            if (cantidad <= 0 || precio <= 0 || !Double.isFinite(precio * cantidad)) {
                return error("Ingrese cantidad y precio de función válidos");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese una cantidad entera");
        }
        return true;
    }

    @FXML
    /**
     * Valida, crea el objeto si es nuevo y copia los campos mediante setters.
     * Marca saved=true y cierra; el insert/update lo hará el controlador de tabla.
     */
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Venta();
        }
        registro.setFuncion(cbFuncion.getValue());
        registro.setCliente(txtCliente.getText().trim());
        registro.setCantidadAsientos(Integer.parseInt(txtCantidad.getText().trim()));
        // Total de esta venta = cantidad de entradas multiplicada por precio de la función.
        registro.setTotalPagar(registro.getCantidadAsientos() * registro.getFuncion().getPrecio());
        saved = true;
        cerrar();
    }

    @FXML
    /**
     * Cierra el formulario sin marcarlo como guardado.
     */
    private void handleCancel() {
        cerrar();
    }

    /**
     * Obtiene la ventana a partir de un control y la cierra.
     * Control → Scene → Window; el cast Stage permite usar close().
     */
    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra el motivo por el que falló la validación y devuelve false.
     */
    private boolean error(String mensaje) {
        utilAlerts.mostrarAlerts("Datos incorrectos", mensaje, "WARNING").showAndWait();
        return false;
    }
}
