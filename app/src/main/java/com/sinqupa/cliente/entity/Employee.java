package com.sinqupa.cliente.entity;

public class Employee {
    private boolean activated;
    private Integer code;
    private Double latitudeTravel;
    private Double longitudeTravel;

    public Employee() {
    }

    public Employee(boolean activated, Integer code, Double latitudeTravel, Double longitudeTravel) {
        this.activated = activated;
        this.code = code;
        this.latitudeTravel = latitudeTravel;
        this.longitudeTravel = longitudeTravel;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Double getLatitudeTravel() {
        return latitudeTravel;
    }

    public void setLatitudeTravel(Double latitudeTravel) {
        this.latitudeTravel = latitudeTravel;
    }

    public Double getLongitudeTravel() {
        return longitudeTravel;
    }

    public void setLongitudeTravel(Double longitudeTravel) {
        this.longitudeTravel = longitudeTravel;
    }
}
