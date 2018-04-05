package com.CmpE275.FlightReservation.FlightReservation.Passenger;

import com.CmpE275.FlightReservation.FlightReservation.Reservation.Reservation;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Entity
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passengerNumber;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String phone;
    @OneToMany(targetEntity=Reservation.class, cascade=CascadeType.ALL)
    private List<Reservation> reservations;

    public Passenger(){

    }

    public Passenger(String firstname, String lastname, int age, String gender, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.reservations = reservations;
    }

    public int getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(int passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
