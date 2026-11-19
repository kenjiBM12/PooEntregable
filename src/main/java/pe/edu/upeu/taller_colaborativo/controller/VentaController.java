package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;
import pe.edu.upeu.taller_colaborativo.modelo.Sala;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VentaController {

    @FXML
    private ComboBox<Funcion> cboFuncion;
    @FXML
    private Label lblSala;
    @FXML
    private Label lblPrecioButaca;
    @FXML
    private Label lblResumenButacas;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMensaje;
    @FXML
    private ScrollPane scrollButacas;
    @FXML
    private GridPane gridButacas;

    // Medidas de la cuadricula. El ancho de cada butaca sale de repartir el panel,
    // asi que el minimo y el maximo viven solo aca (el CSS no declara min-width)
    private static final double ANCHO_ETIQUETA = 22;
    private static final double ANCHO_MINIMO = 20;
    private static final double ANCHO_MAXIMO = 36;
    private static final double ALTO_BUTACA = 26;

    private final List<String> butacasElegidas = new ArrayList<>();

    @FXML
    public void initialize() {
        // El combo se llena con la lista de funciones del modulo Funciones
        cboFuncion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Funcion funcion) {
                return funcion == null ? "" : funcion.getPelicula().getTitulo() + " · " + funcion.getHora();
            }

            @Override
            public Funcion fromString(String texto) {
                return null;
            }
        });
        cboFuncion.setItems(FuncionController.getListaFunciones());
        cboFuncion.getSelectionModel().selectFirst();

        cboFuncion.valueProperty().addListener((obs, anterior, nueva) -> {
            butacasElegidas.clear();
            refrescar();
        });

        // Si cambia el ancho del panel se vuelven a calcular las butacas
        scrollButacas.viewportBoundsProperty().addListener((obs, anterior, nuevo) -> {
            if (anterior.getWidth() != nuevo.getWidth()) {
                refrescar();
            }
        });

        refrescar();
    }

    // Vuelve a pintar los datos de la funcion, las butacas y el resumen
    private void refrescar() {
        lblMensaje.setText("");
        Funcion funcion = cboFuncion.getValue();

        if (funcion == null) {
            lblSala.setText("Todavía no hay funciones programadas.");
            lblPrecioButaca.setText("Precio por butaca: -");
        } else {
            lblSala.setText(funcion.getDescripcion());
            lblPrecioButaca.setText(String.format(Locale.US, "Precio por butaca: S/ %.2f",
                    funcion.getPrecio()));
        }

        dibujarButacas();
        actualizarResumen();
    }

    // La cuadricula sale de las filas y columnas de la sala de la funcion
    private void dibujarButacas() {
        gridButacas.getChildren().clear();
        Funcion funcion = cboFuncion.getValue();

        if (funcion == null) {
            return;
        }

        Sala sala = funcion.getSala();
        Set<String> ocupadas = VentaListaController.butacasVendidasDe(funcion);
        int columnas = sala.getColumnas();
        double ancho = anchoButaca(scrollButacas.getViewportBounds().getWidth(), columnas);

        for (int fila = 0; fila < sala.getFilas(); fila++) {
            String letra = String.valueOf((char) ('A' + fila));

            Label etiquetaFila = new Label(letra);
            etiquetaFila.getStyleClass().add("fila-etiqueta");
            etiquetaFila.setMinWidth(ANCHO_ETIQUETA);
            gridButacas.add(etiquetaFila, 0, fila);

            for (int columna = 1; columna <= columnas; columna++) {
                String codigo = letra + columna;
                Button butaca = new Button(String.valueOf(columna));
                butaca.getStyleClass().add("butaca");
                butaca.setMinWidth(ANCHO_MINIMO);
                butaca.setPrefSize(ancho, ALTO_BUTACA);

                if (ocupadas.contains(codigo)) {
                    butaca.getStyleClass().add("butaca-ocupada");
                    butaca.setDisable(true);
                } else {
                    butaca.setOnAction(e -> cambiarButaca(butaca, codigo));
                }

                gridButacas.add(butaca, columna, fila);
            }
        }
    }

    // Reparte el ancho del panel entre las columnas de butacas de la sala
    private double anchoButaca(double anchoPanel, int columnas) {
        double separacion = gridButacas.getHgap();
        double disponible = anchoPanel - ANCHO_ETIQUETA - separacion * (columnas + 1);

        if (disponible <= 0) {
            return ANCHO_MINIMO;
        }
        return Math.max(ANCHO_MINIMO, Math.min(ANCHO_MAXIMO, disponible / columnas));
    }

    // Si la butaca ya estaba elegida la quita, si no la agrega
    private void cambiarButaca(Button butaca, String codigo) {
        if (butacasElegidas.remove(codigo)) {
            butaca.getStyleClass().remove("butaca-elegida");
        } else {
            butacasElegidas.add(codigo);
            butaca.getStyleClass().add("butaca-elegida");
        }
        lblMensaje.setText("");
        actualizarResumen();
    }

    private void actualizarResumen() {
        lblResumenButacas.setText(butacasElegidas.isEmpty()
                ? "Todavía no elegiste ninguna butaca."
                : String.join(", ", butacasElegidas));

        Funcion funcion = cboFuncion.getValue();
        double total = funcion == null ? 0 : butacasElegidas.size() * funcion.getPrecio();
        lblTotal.setText(String.format(Locale.US, "S/ %.2f", total));
    }

    @FXML
    private void handleConfirmar() {
        Funcion funcion = cboFuncion.getValue();

        if (funcion == null) {
            lblMensaje.setText("No hay funciones programadas para vender entradas.");
            return;
        }
        if (butacasElegidas.isEmpty()) {
            lblMensaje.setText("Elija al menos una butaca para continuar.");
            return;
        }

        Alert alerta = UtilAlerts.mostrarAlerts("Compra registrada",
                "Película: " + funcion.getPelicula().getTitulo()
                        + "\nFunción: " + funcion.getDescripcion()
                        + "\nButacas: " + lblResumenButacas.getText()
                        + "\nTotal: " + lblTotal.getText(),
                "INFORMATION");
        alerta.showAndWait();

        // La venta queda registrada y con eso sus butacas pasan a estar ocupadas
        VentaListaController.registrar(funcion, String.join(", ", butacasElegidas),
                butacasElegidas.size() * funcion.getPrecio());

        butacasElegidas.clear();
        refrescar();
    }
}
