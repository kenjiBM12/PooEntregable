package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 3: fila de un gráfico de Reportes.
 *
 * Se usa para las tres consultas nuevas: ventas por dia, peliculas mas
 * vendidas y ocupacion por funcion. "etiqueta" es el texto del eje X,
 * "cantidad" los boletos y "valor" el dinero o el porcentaje, según la consulta.
 */
public class Estadistica {

    private String etiqueta = "";
    private int cantidad;
    private double valor;

    public Estadistica() {
    }

    public Estadistica(String etiqueta, int cantidad, double valor) {
        this.etiqueta = etiqueta;
        this.cantidad = cantidad;
        this.valor = valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
