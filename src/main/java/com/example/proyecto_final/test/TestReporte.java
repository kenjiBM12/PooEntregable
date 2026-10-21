// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestReporte {
    static IVentaDao dao = new VentaDaoImpl();

    /**
     * Consulta las ventas de la fecha y muestra filas, entradas y suma en consola.
     */
    public static void listar(LocalDate fecha) {
        List<Venta> lista = dao.listByDate(fecha);
        // Acumula importes del reporte a partir de los totales guardados de cada venta.
        BigDecimal total = BigDecimal.ZERO;
        long entradas = 0;
        System.out.println("REPORTE DEL DÍA: " + fecha + " (hora de Perú)");
        for (Venta v : lista) {
            System.out.println(v.getIdventa() + " | " + v.getCliente() + " | "
                    + v.getFuncion().getPelicula().getTitulo() + " | "
                    + v.getCantidadAsientos() + " | S/ " + v.getTotalPagar()
                    + " | " + v.getHoraVenta());
            total = total.add(BigDecimal.valueOf(v.getTotalPagar()));
            entradas += v.getCantidadAsientos();
        }
        System.out.println("Ventas: " + lista.size());
        System.out.println("Entradas: " + entradas);
        System.out.printf(Locale.US, "Total: S/ %.2f%n", total);
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        LocalDate fecha = args.length == 0
                ? LocalDate.now(ZoneId.of("America/Lima")) : LocalDate.parse(args[0]);
        listar(fecha);
    }
}
