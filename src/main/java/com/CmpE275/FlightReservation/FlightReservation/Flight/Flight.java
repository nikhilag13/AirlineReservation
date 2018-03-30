package com.CmpE275.FlightReservation.FlightReservation.Flight;

import com.CmpE275.FlightReservation.FlightReservation.Passenger.Passenger;
import com.CmpE275.FlightReservation.FlightReservation.Plane.Plane;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
public class Flight {
    @Id
    private String flightNumber; // Each flight has a unique flight number.
    private double price;
    private String fromPlace;
    private String toPlace;
    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @Embedded
    private Plane plane;  // Embedded

    @ManyToMany(targetEntity=Passenger.class)
    private List<Passenger> passengers;


    public Flight(){

    }

    public Flight(String flightNumber, double price, String fromPlace, String toPlace, Date departureTime, Date arrivalTime, int seatsLeft, String description, Plane plane, List<Passenger> passengers) {
        this.flightNumber = flightNumber;
        this.price = price;
        this.fromPlace = fromPlace;
        this.toPlace = toPlace;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsLeft = seatsLeft;
        this.description = description;
        this.plane = plane;
        this.passengers = passengers;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public double getPrice() {
        return price;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFrom() {
        return fromPlace;
    }

    public void setFrom(String from) {
        this.fromPlace = from;
    }

    public String getTo() {
        return toPlace;
    }

    public void setTo(String to) {
        this.toPlace = to;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
