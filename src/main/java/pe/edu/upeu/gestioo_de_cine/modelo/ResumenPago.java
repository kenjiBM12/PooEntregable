package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 2: fila del cierre de caja por forma de pago.
 *
 * El reporte usa un objeto de esta clase por cada forma de pago encontrada
 * en el rango de fechas (EFECTIVO, TARJETA, YAPE...). Asi el administrador
 * sabe cuanto dinero entro por cada medio y puede cuadrar la caja.
 *
 * No se usan propiedades de JavaFX porque este objeto no se edita: solo se
 * muestra en una tabla de lectura.
 */
public class ResumenPago {

    private String metodo = "";
    private int boletos;
    private double monto;

    public ResumenPago() {
    }

    public ResumenPago(String metodo, int boletos, double monto) {
        this.metodo = metodo;
        this.boletos = boletos;
        this.monto = monto;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public int getBoletos() {
        return boletos;
    }

    public void setBoletos(int boletos) {
        this.boletos = boletos;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
