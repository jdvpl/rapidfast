package com.jdrapid.rapidfast.models;

public class Cliente {
    String id;
    String nombre;
    String correo;
    String imagen;
    String telefono;

    public Cliente(){

    }

    public Cliente(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    public Cliente(String id, String nombre, String correo, String imagen, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.imagen = imagen;
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
