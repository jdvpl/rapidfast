package com.jdrapid.rapidfast.models;

public class Info {
    double km;
    double min;
    double minnoche;
    double kmnoche;

    public Info() {
    }

    public Info(double km, double min, double minnoche, double kmnoche) {
        this.km = km;
        this.min = min;
        this.minnoche = minnoche;
        this.kmnoche = kmnoche;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMinnoche() {
        return minnoche;
    }

    public void setMinnoche(double minnoche) {
        this.minnoche = minnoche;
    }

    public double getKmnoche() {
        return kmnoche;
    }

    public void setKmnoche(double kmnoche) {
        this.kmnoche = kmnoche;
    }
}
