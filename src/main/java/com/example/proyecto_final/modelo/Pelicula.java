package com.example.proyecto_final.modelo;

public class Pelicula {
    private int id;
    private String titulo;
    private String genero;
    private int duracionMin;
    private String clasificacion;
    // todo Espectador de mas 14 años

    public Pelicula() {
    }

    public Pelicula(int id, String titulo, String genero, int duracionMin, String clasificacion) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMin = duracionMin;
        this.clasificacion = clasificacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = duracionMin;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }
}