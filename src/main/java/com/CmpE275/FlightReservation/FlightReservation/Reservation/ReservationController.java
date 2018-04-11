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

    /**
     * Returns an HTTP response entity which is the reservation
     * details. The reservation id is passed from client and are
     * retrived and sent to the client
     *
     * @param  number  reservation number
     * @return code status and the reservation details in JSON format or error message
     */
    @RequestMapping(value="/reservation/{number}", method= RequestMethod.GET)
    public ResponseEntity<?> getReservation(
            @PathVariable int number) {

        return reservationService.getReservation(number);
    }

    /**
     * Deletes the reservation data from the database
     * reservation id is passed from the client
     *
     * @param  number  Unique id of the reservation
     * @return  code status and the confirmation or error message
     */
    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(
            @PathVariable int number) {

        return reservationService.deleteReservation(number);
    }

    /**
     * Returns an HTTP response entity which is the reservation
     * details. The reservation details are passed from client and are
     * persisted into the database.
     *
     * @param  passengerId  passengerId of the passenger
     * @param  flightLists flight Lists of the passenger
     * @return code status and the newly created reservation details or error message
     */
    @RequestMapping(value="/reservation", method=RequestMethod.POST)
    public ResponseEntity<?> makeReservation(
            @RequestParam int passengerId,
            @RequestParam("flightLists") List<Flight> flightLists
    ) {

        return reservationService.makeReservation(passengerId, flightLists);
    }

    /**
     * Returns an HTTP response entity which is the passenger
     * details. The passenger details are passed from client and are
     * persisted into the database.
     *
     * @param  passengerId  passengerId of the passenger
     * @param  origin origin of the flights
     * @param  to to of the flights
     * @param  flightNumber gender of the flight Number
     * @return code status and the serach criteria details or error message
     */
    @RequestMapping(value="/reservation", method= RequestMethod.GET)
    public ResponseEntity<?> searchReservation(
            @RequestParam int passengerId,
            @RequestParam String origin,
            @RequestParam String to,
            @RequestParam String flightNumber
    ) throws JSONException {

        return reservationService.searchReservation(passengerId,origin,to,flightNumber);
    }

    /**
     * Returns an HTTP response entity which is the reservation
     * details. The reservation details are passed from client and are
     * updated and persisted into the database.
     *
     * @param  reservationNumber  reservation Number of the reservation
     * @param  flightsToAdd Flights to be added
     * @param  flightsToRemove Flights to be removed
     * @return code status and the updated reservation details or error message
     */
    @RequestMapping(value="/reservation/{reservationNumber}", method=RequestMethod.POST)
    public ResponseEntity<?> updateReservation(
            @PathVariable int reservationNumber,
            @RequestParam("flightsAdded") List<Flight> flightsToAdd,
            @RequestParam("flightsRemoved") List<Flight> flightsToRemove
    ) throws JSONException {

        return reservationService.updateReservation(reservationNumber,flightsToAdd, flightsToRemove);
    }
}


