package com.jdrapid.rapidfastDriver.models;

public class InfoCelular {
    String policiaCel;
    String secretariaCel;
    String soporteCel;

    public InfoCelular() {
    }

    public InfoCelular(String policiaCel, String secretariaCel, String soporteCel) {
        this.policiaCel = policiaCel;
        this.secretariaCel = secretariaCel;
        this.soporteCel = soporteCel;
    }

    public String getPoliciaCel() {
        return policiaCel;
    }

    public void setPoliciaCel(String policiaCel) {
        this.policiaCel = policiaCel;
    }

    public String getSecretariaCel() {
        return secretariaCel;
    }

    public void setSecretariaCel(String secretariaCel) {
        this.secretariaCel = secretariaCel;
    }

    public String getSoporteCel() {
        return soporteCel;
    }

    public void setSoporteCel(String soporteCel) {
        this.soporteCel = soporteCel;
    }
}
