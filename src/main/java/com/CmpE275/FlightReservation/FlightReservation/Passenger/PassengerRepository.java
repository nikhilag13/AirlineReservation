package com.CmpE275.FlightReservation.FlightReservation.Passenger;

import org.springframework.data.repository.CrudRepository;

public interface PassengerRepository extends CrudRepository<Passenger, String> {

    public Passenger findByPassengerNumber(int passengerNumber);

    public Passenger findByPhone(String phone);
}
