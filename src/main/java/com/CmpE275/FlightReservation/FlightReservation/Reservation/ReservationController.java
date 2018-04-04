package com.CmpE275.FlightReservation.FlightReservation.Reservation;

import com.CmpE275.FlightReservation.FlightReservation.Flight.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@Transactional
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @RequestMapping(value="/reservation/{number}", method= RequestMethod.GET)
    public ResponseEntity<?> getReservation(@PathVariable int number) {

        return reservationService.getReservation(number);
    }

    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(@PathVariable int number) {

        return reservationService.deleteReservation(number);
    }

    @RequestMapping(value="/reservation", method=RequestMethod.POST)// Chaged it form Applicatiion_JSON
    public ResponseEntity<?> makeReservation(
            @RequestParam int passengerId,
            @RequestParam("flightLists") List<Flight> flightLists
    ) {

        return reservationService.makeReservation(passengerId, flightLists);
    }

}
