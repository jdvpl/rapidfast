package com.jdrapid.rapidfastDriver.models;

public class Conductor {
    String id;
    String nombre;
    String correo;
    String cedula;
    String marcaVehiculo;
    String PlacaVehiculo;
    String imagen;
    String telefono;

    public Conductor() {
    }

    public Conductor(String id, String nombre, String correo, String cedula, String marcaVehiculo, String placaVehiculo, String imagen, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.cedula = cedula;
        this.marcaVehiculo = marcaVehiculo;
        PlacaVehiculo = placaVehiculo;
        this.imagen = imagen;
        this.telefono = telefono;
    }

    public Conductor(String id, String nombre, String correo, String cedula, String marcaVehiculo, String placaVehiculo, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.cedula = cedula;
        this.marcaVehiculo = marcaVehiculo;
        PlacaVehiculo = placaVehiculo;
        this.imagen = imagen;
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public String getPlacaVehiculo() {
        return PlacaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        PlacaVehiculo = placaVehiculo;
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
