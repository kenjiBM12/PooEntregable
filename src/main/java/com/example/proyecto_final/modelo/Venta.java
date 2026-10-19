package com.example.proyecto_final.modelo;

public class Venta {
    private int id;
    private int idFuncion;
    private String cliente;
    private int cantidadAsientos;
    private double totalPagar;
    private String fechaVenta;

    public Venta() {
    }

    public Venta(int id, int idFuncion, String cliente, int cantidadAsientos, double totalPagar, String fechaVenta) {
        this.id = id;
        this.idFuncion = idFuncion;
        this.cliente = cliente;
        this.cantidadAsientos = cantidadAsientos;
        this.totalPagar = totalPagar;
        this.fechaVenta = fechaVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public int getCantidadAsientos() {
        return cantidadAsientos;
    }

    public void setCantidadAsientos(int cantidadAsientos) {
        this.cantidadAsientos = cantidadAsientos;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
}