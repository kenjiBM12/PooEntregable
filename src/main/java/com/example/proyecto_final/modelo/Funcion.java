package com.example.proyecto_final.modelo;

public class Funcion {
    private int id;
    private int idPelicula;
    private int idSala;
    private String horario; // Ej: "18:30 PM"
    private double precio;

    public Funcion() {
    }

    public Funcion(int id, int idPelicula, int idSala, String horario, double precio) {
        this.id = id;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
        this.horario = horario;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}