package com.CmpE275.FlightReservation.FlightReservation.Plane;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Embeddable
public class Plane {
    @Id
    private String number;
    private int capacity;
    private String model;
    private String manufacturer;
    private int year;

    public Plane(){

    }


    public Plane(String number, int capacity, String model, String manufacturer, int year) {
        this.number = number;
        this.capacity = capacity;
        this.model = model;
        this.manufacturer = manufacturer;
        this.year = year;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
