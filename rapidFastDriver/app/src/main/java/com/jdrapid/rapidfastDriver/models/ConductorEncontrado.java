package com.jdrapid.rapidfastDriver.models;

public class ConductorEncontrado {

    private String idConductor;
    private String idClienteSolicitud;

    public ConductorEncontrado() {
    }

    public ConductorEncontrado(String idConductor, String idClienteSolicitud) {
        this.idConductor = idConductor;
        this.idClienteSolicitud = idClienteSolicitud;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getIdClienteSolicitud() {
        return idClienteSolicitud;
    }

    public void setIdClienteSolicitud(String idClienteSolicitud) {
        this.idClienteSolicitud = idClienteSolicitud;
    }
}
