package pe.edu.upeu.gestioo_de_cine.utils;

import pe.edu.upeu.gestioo_de_cine.modelo.Venta;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * MEJORA 9: comprobante de venta reimprimible en HTML.
 *
 * El comprobante .txt se sigue guardando como respaldo, pero ademas se genera
 * un HTML con los datos de la compra: se abre en cualquier navegador y se
 * imprime sin instalar nada. El codigo de compra es el nombre del archivo, asi
 * que reimprimir es simplemente volver a generarlo.
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
     * Escribe el comprobante en HTML y devuelve el archivo generado.
     * Si no se puede escribir devuelve null (el llamador avisa y sigue).
     */
    public static File generarHtml(String codigo, String textoComprobante) {
        String nombre = "comprobante_" + limpiar(codigo) + ".html";
        File archivo = new File(carpeta(), nombre);
        try {
            Files.writeString(archivo.toPath(), html(codigo, textoComprobante),
                    StandardCharsets.UTF_8);
            return archivo;
        } catch (Exception e) {
            Log.error("No se pudo generar el comprobante HTML " + nombre, e);
            return null;
        }
    }

    //el HTML usa el texto del comprobante tal cual, escapando los caracteres especiales
    private static String html(String codigo, String textoComprobante) {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<title>Comprobante " + escapar(codigo) + "</title>"
                + "<style>"
                + "body{font-family:Consolas,monospace;background:#f4f4f4;padding:24px;}"
                + ".ticket{background:#fff;max-width:460px;margin:0 auto;padding:22px 26px;"
                + "border:1px solid #ddd;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.08);}"
                + "h1{font-size:18px;letter-spacing:2px;text-align:center;margin:0 0 4px;}"
                + ".sub{text-align:center;color:#777;font-size:12px;margin-bottom:14px;}"
                + "pre{font-size:13px;line-height:1.45;white-space:pre-wrap;}"
                + ".pie{margin-top:16px;text-align:center;color:#777;font-size:11px;}"
                + "@media print{body{background:#fff;padding:0}.ticket{border:0;box-shadow:none}}"
                + "</style></head><body><div class=\"ticket\">"
                + "<h1>CINEMAX</h1><div class=\"sub\">Sistema de venta de entradas - UPeU</div>"
                + "<pre>" + escapar(textoComprobante) + "</pre>"
                + "<div class=\"pie\">Comprobante generado por el sistema</div>"
                + "</div></body></html>";
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

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "sin_codigo";
        }
        return valor.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
