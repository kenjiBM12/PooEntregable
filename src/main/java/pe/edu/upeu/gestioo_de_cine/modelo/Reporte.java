package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 3: modelo del reporte de cierre de caja.
 *
 * Esta clase guarda SOLO los totales que calcula el ReporteDaoImpl con SQL.
 * No se usan propiedades de JavaFX porque este objeto no se muestra en una
 * tabla: los controladores leen sus valores y los ponen en Labels.
 *
 * Los boletos "activos" son los que cuentan como venta; los "anulados" ya
 * no cuentan como ingreso, pero se informan aparte para el cierre de caja.
 */
public class Reporte {

    //rango de fechas consultado (texto yyyy-MM-dd, solo para mostrarlo)
    private String desde = "";
    private String hasta = "";

    //cantidad de boletos
    private int boletosActivos;
    private int boletosAnulados;

    //dinero
    private double ingresos;       //suma de boletos activos
    private double montoAnulado;   //suma de boletos anulados

    public Reporte() {
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public int getBoletosActivos() {
        return boletosActivos;
    }

    public void setBoletosActivos(int boletosActivos) {
        this.boletosActivos = boletosActivos;
    }

    public int getBoletosAnulados() {
        return boletosAnulados;
    }

    public void setBoletosAnulados(int boletosAnulados) {
        this.boletosAnulados = boletosAnulados;
    }

    public double getIngresos() {
        return ingresos;
    }

    public void setIngresos(double ingresos) {
        this.ingresos = ingresos;
    }

    public double getMontoAnulado() {
        return montoAnulado;
    }

    public void setMontoAnulado(double montoAnulado) {
        this.montoAnulado = montoAnulado;
    }

    //el total neto es simplemente lo que realmente ingreso a caja
    public double getTotalNeto() {
        return ingresos;
    }
}
