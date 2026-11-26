package pe.edu.upeu.gestioo_de_cine.utils;

import pe.edu.upeu.gestioo_de_cine.modelo.Venta;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * MEJORA 9: comprobante de venta reimprimible.
 *
 * El comprobante se guarda como archivo de texto (.txt) con los datos de la
 * compra: se abre con el Bloc de notas, se imprime o se envia al cliente.
 * Se usa solo lo visto en el curso (File, Files y texto), sin HTML.
 * Reimprimir es simplemente volver a generarlo desde la base.
 */
public class Comprobante {

    /** Carpeta donde quedan los comprobantes. */
    public static File carpeta() {
        File carpeta = new File(ConexionDB.carpetaSistema(), "comprobantes");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return carpeta;
    }

    /**
     * Arma el texto del comprobante a partir de los boletos de una compra.
     * Cada fila de venta es un boleto, por eso se listan todos.
     */
    public static String texto(List<Venta> boletos) {
        if (boletos == null || boletos.isEmpty()) {
            return "Sin boletos para mostrar.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CINEMAX - Comprobante de venta\n");
        sb.append("========================================\n");
        Venta primera = boletos.get(0);
        sb.append("Compra: ").append(codigo(primera)).append('\n');
        sb.append("Cliente: ").append(texto(primera.getCliente())).append('\n');
        if (primera.getFechaVenta() != null) {
            sb.append("Fecha de venta: ").append(primera.getFechaVenta()).append('\n');
        }
        sb.append("Forma de pago: ").append(texto(primera.getMetodoPago())).append('\n');
        sb.append("========================================\n");
        double total = 0;
        for (Venta v : boletos) {
            String pelicula = v.getFuncion() != null && v.getFuncion().getPelicula() != null
                    ? v.getFuncion().getPelicula().getTitulo() : "";
            String sala = v.getFuncion() != null && v.getFuncion().getSala() != null
                    ? v.getFuncion().getSala().getEtiqueta() : "";
            String horario = v.getFuncion() != null ? v.getFuncion().getHorario() : "";
            String butaca = v.getButaca() != null ? String.valueOf(v.getButaca().getNumero()) : "";
            sb.append("Boleto ").append(codigo(v)).append(" - ").append(pelicula).append('\n');
            sb.append("   ").append(sala).append("  ").append(horario)
                    .append("  Butaca ").append(butaca).append('\n');
            sb.append("   Importe: S/ ").append(String.format("%.2f", v.getTotalPagar())).append('\n');
            total = total + v.getTotalPagar();
        }
        sb.append("========================================\n");
        sb.append("TOTAL: S/ ").append(String.format("%.2f", total)).append('\n');
        sb.append("Gracias por su compra.\n");
        return sb.toString();
    }

    /**
     * Escribe el comprobante como archivo .txt y devuelve el archivo generado.
     * Si no se puede escribir devuelve null (el llamador avisa y sigue).
     */
    public static File guardar(String codigo, String textoComprobante) {
        String nombre = "comprobante_" + limpiar(codigo) + ".txt";
        File archivo = new File(carpeta(), nombre);
        try {
            Files.writeString(archivo.toPath(), textoComprobante, StandardCharsets.UTF_8);
            return archivo;
        } catch (Exception e) {
            Log.error("No se pudo guardar el comprobante " + nombre, e);
            return null;
        }
    }

    /** Codigo visible de la compra: el guardado o "V-" + idventa si es vieja. */
    public static String codigo(Venta v) {
        if (v == null) {
            return "";
        }
        if (v.getCodigoCompra() != null && !v.getCodigoCompra().isBlank()) {
            return v.getCodigoCompra();
        }
        return "V-" + v.getIdventa();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private static String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "sin_codigo";
        }
        return valor.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
