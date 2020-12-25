package com.jdrapid.rapidfastDriver.models;

import java.util.Date;

public class Afiliados {
    String idConductor;
    String idAfiliado;
    String cedula;
    String nombre;
    String celular;
    String fecha;
    String estado;

    public Afiliados() {
    }

    public Afiliados(String idAfiliado, String cedula, String nombre, String celular, String fecha,String estado,String idConductor) {
        this.idAfiliado = idAfiliado;
        this.idConductor=idConductor;
        this.cedula = cedula;
        this.nombre = nombre;
        this.celular = celular;
        this.fecha = fecha;
        this.estado=estado;
    }

    public String getIdAfiliado() {
        return idAfiliado;
    }

    public void setIdAfiliado(String idAfiliado) {
        this.idAfiliado = idAfiliado;
    }

    public String getIdConductor() {
        return idConductor;
    }
    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
