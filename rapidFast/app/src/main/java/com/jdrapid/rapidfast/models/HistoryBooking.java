package com.jdrapid.rapidfast.models;

public class HistoryBooking {
    String idHistorialSolicitud;
    String idCliente;
    String idConductor;
    String Destino;
    String Origen;
    String tiempo;
    String distanciaKm;
    String estado;
    double origenLat;
    double origenLong;
    double destinoLat;
    double destinoLong;
    double calificacionCliente;
    double calificacionConductor;
    long timestamp;

    public HistoryBooking(){

    }

    public HistoryBooking(String idHistorialSolicitud, String idCliente, String idConductor, String destino, String origen, String tiempo, String distanciaKm, String estado, double origenLat, double origenLong, double destinoLat, double destinoLong) {
        this.idHistorialSolicitud = idHistorialSolicitud;
        this.idCliente = idCliente;
        this.idConductor = idConductor;
        Destino = destino;
        Origen = origen;
        this.tiempo = tiempo;
        this.distanciaKm = distanciaKm;
        this.estado = estado;
        this.origenLat = origenLat;
        this.origenLong = origenLong;
        this.destinoLat = destinoLat;
        this.destinoLong = destinoLong;
    }

    public String getIdHistorialSolicitud() {
        return idHistorialSolicitud;
    }

    public void setIdHistorialSolicitud(String idHistorialSolicitud) {
        this.idHistorialSolicitud = idHistorialSolicitud;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getDestino() {
        return Destino;
    }

    public void setDestino(String destino) {
        Destino = destino;
    }

    public String getOrigen() {
        return Origen;
    }

    public void setOrigen(String origen) {
        Origen = origen;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(String distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(double origenLat) {
        this.origenLat = origenLat;
    }

    public double getOrigenLong() {
        return origenLong;
    }

    public void setOrigenLong(double origenLong) {
        this.origenLong = origenLong;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLong() {
        return destinoLong;
    }

    public void setDestinoLong(double destinoLong) {
        this.destinoLong = destinoLong;
    }

    public double getCalificacionCliente() {
        return calificacionCliente;
    }

    public void setCalificacionCliente(double calificacionCliente) {
        this.calificacionCliente = calificacionCliente;
    }

    public double getCalificacionConductor() {
        return calificacionConductor;
    }

    public void setCalificacionConductor(double calificacionConductor) {
        this.calificacionConductor = calificacionConductor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
