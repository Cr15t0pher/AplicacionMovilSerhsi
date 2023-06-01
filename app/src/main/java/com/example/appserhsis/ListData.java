package com.example.appserhsis;

public class ListData {
    String nombre,descripcion, costo,id;
    String refacciones;
    int image;
    public ListData(String id, String nombre,String descripcion, String costo, String refacciones, int image) {
        this.nombre = nombre;
        this.costo = costo;
        this.refacciones = refacciones;
        this.image = image;
        this.descripcion=descripcion;
        this.id=id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefacciones() {
        return refacciones;
    }

    public void setRefacciones(String refacciones) {
        this.refacciones = refacciones;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}




