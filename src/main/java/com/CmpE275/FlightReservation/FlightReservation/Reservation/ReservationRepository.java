package com.CmpE275.FlightReservation.FlightReservation.Reservation;

import com.CmpE275.FlightReservation.FlightReservation.Passenger.Passenger;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, String> {

    Reservation findByReservationNumber(int reservationNumber);

    List<Reservation> findByPassenger(Passenger passenger);

}
