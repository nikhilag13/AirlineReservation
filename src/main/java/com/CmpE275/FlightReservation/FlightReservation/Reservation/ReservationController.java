package com.CmpE275.FlightReservation.FlightReservation.Reservation;

import com.CmpE275.FlightReservation.FlightReservation.Flight.Flight;
import org.json.JSONException;
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
    public ResponseEntity<?> getReservation(
            @PathVariable int number) {

        return reservationService.getReservation(number);
    }

    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(
            @PathVariable int number) {

        return reservationService.deleteReservation(number);
    }

    @RequestMapping(value="/reservation", method=RequestMethod.POST)
    public ResponseEntity<?> makeReservation(
            @RequestParam int passengerId,
            @RequestParam("flightLists") List<Flight> flightLists
    ) {

        return reservationService.makeReservation(passengerId, flightLists);
    }

    //TODO:Manvitha's code

    @RequestMapping(value="/reservation/", method= RequestMethod.GET)
    public ResponseEntity<?> searchReservation(
            @RequestParam int passengerId,
            @RequestParam String origin,
            @RequestParam String to,
            @RequestParam String flightNumber
    ) throws JSONException {

        return reservationService.searchReservation(passengerId,origin,to,flightNumber);
    }

    @RequestMapping(value="/reservation/{reservationNumber}", method=RequestMethod.POST)
    public ResponseEntity<?> updateReservation(
            @PathVariable int reservationNumber,
            @RequestParam("flightsAdded") List<Flight> flightsToAdd,
            @RequestParam("flightsRemoved") List<Flight> flightsToRemove
    ) throws JSONException {

        return reservationService.updateReservation(reservationNumber,flightsToAdd, flightsToRemove);
    }
}


