package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;
import pe.edu.upeu.taller_colaborativo.modelo.Pelicula;

import java.net.URL;
import java.util.stream.Collectors;

public class CarteleraController {

    private static final String RUTA_IMG = "/pe/edu/upeu/taller_colaborativo/img/";
    private static final String POSTER_DEFECTO = "poster-default.png";
    private static final double ANCHO_POSTER = 208;
    private static final double ALTO_POSTER = 291;

    @FXML
    private FlowPane contenedorPeliculas;
    @FXML
    private ScrollPane scrollCartelera;

    @FXML
    public void initialize() {
        // Hace que las tarjetas salten de fila segun el ancho de la ventana
        contenedorPeliculas.prefWrapLengthProperty()
                .bind(scrollCartelera.widthProperty().subtract(45));

        // La cartelera se arma con las peliculas registradas que estan activas
        for (Pelicula pelicula : PeliculaController.getListaPeliculas()) {
            if (pelicula.getEstado().equals("Activa")) {
                contenedorPeliculas.getChildren().add(crearTarjeta(pelicula));
            }
        }
    }

    private VBox crearTarjeta(Pelicula pelicula) {
        ImageView poster = new ImageView(cargarPoster(pelicula.getPoster()));
        poster.getStyleClass().add("poster");

        Label clasificacion = new Label(pelicula.getClasificacion());
        clasificacion.getStyleClass().add("clasificacion");

        Label titulo = new Label(pelicula.getTitulo());
        titulo.getStyleClass().add("tarjeta-titulo");
        titulo.setWrapText(true);

        Label detalle = new Label(pelicula.getGenero() + "  ·  " + pelicula.getDuracion() + " min");
        detalle.getStyleClass().add("tarjeta-detalle");

        Label horario = new Label(horariosDe(pelicula));
        horario.getStyleClass().add("tarjeta-horario");
        horario.setWrapText(true);

        // Espacio para que el boton siempre quede abajo
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);

        Button boton = new Button("Elegir butacas");
        boton.getStyleClass().add("boton-tarjeta");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(e -> MainController.getInstancia().showVenta());

        VBox tarjeta = new VBox(8);
        tarjeta.getStyleClass().add("tarjeta");
        tarjeta.setPrefWidth(240);
        tarjeta.getChildren().addAll(poster, clasificacion, titulo, detalle, horario, espacio, boton);
        return tarjeta;
    }

    // Carga el poster ya escalado; si no tiene o el archivo no existe usa el de por defecto
    private Image cargarPoster(String archivo) {
        String nombre = (archivo == null || archivo.isEmpty()) ? POSTER_DEFECTO : archivo;
        URL url = getClass().getResource(RUTA_IMG + nombre);

        if (url == null) {
            url = getClass().getResource(RUTA_IMG + POSTER_DEFECTO);
        }
        return new Image(url.toExternalForm(), ANCHO_POSTER, ALTO_POSTER, true, true);
    }

    // Junta los horarios de las funciones de esa pelicula
    private String horariosDe(Pelicula pelicula) {
        String horarios = FuncionController.getListaFunciones().stream()
                .filter(funcion -> pelicula.equals(funcion.getPelicula()))
                .map(Funcion::getHora)
                .collect(Collectors.joining("  ·  "));

        return horarios.isEmpty() ? "Sin funciones programadas" : horarios;
    }
}
