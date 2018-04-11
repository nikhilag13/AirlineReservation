package com.CmpE275.FlightReservation.FlightReservation.Flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@Transactional
public class FlightController {
    @Autowired
    private FlightService flightService;

    /**
     * Returns an HTTP response entity which is the Flight
     * details. The Flight details are passed from client and are
     * persisted into the database.
     *
     * @param  flightNumber  flightNumber of the flight
     * @param  price price of the flight
     * @param  from  origin of the flight
     * @param  to to of the flight
     * @param  departureTime departureTime of the flight
     * @param  arrivalTime  arrivalTime of the flight
     * @param  description description of the flight
     * @param  capacity  capacity of the flight
     * @param  model model of the flight
     * @param  manufacturer manufacturer of the flight
     * @param  manufacturedYear year of the flight
     * @return success code status and the newly created flight details in xml format
     */

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST)
    public ResponseEntity<?> addFlight(@PathVariable String flightNumber,
                                       @RequestParam("price") int price,
                                       @RequestParam("origin") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("departureTime") String departureTime,
                                       @RequestParam("arrivalTime") String arrivalTime,
                                       @RequestParam("description") String description,
                                       @RequestParam("capacity") int capacity,
                                       @RequestParam("model") String model,
                                       @RequestParam("manufacturer") String manufacturer,
                                       @RequestParam("year") int manufacturedYear){

        System.out.println("--------- addNewFlight ----------");
         return flightService.addNewFlight(flightNumber, price, from, to,
                departureTime, arrivalTime, capacity, description,model,manufacturer,manufacturedYear);
    }

    /**
     * Deletes the Flight. The Flight ID is passed from client
     *
     * @param  flightNumber  flightNumber of the flight
     * @return success  if the flight is deleted
     */

    @RequestMapping(value = "/airline/{flightNumber}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFlight(@PathVariable String flightNumber){

        System.out.println("--------- deleteFlight ----------");
        return flightService.deleteFlight(flightNumber);

    }

    /**
     * Gets the Flight. The Flight ID is passed from client
     *
     * @param  flightNumber  flightNumber of the flight
     * @return returns the flight details based on the xml value
     */

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.GET)
    public ResponseEntity<?> getFlight(@PathVariable String flightNumber, @RequestParam(value = "xml", required = false) boolean responseinXml){
        System.out.println("--------- getFlight ----------");
        return flightService.getFlightResponse(flightNumber,responseinXml);

    }

    /**
     * Returns an HTTP response entity which is the Flight
     * details. The Flight details are passed from client and are
     * persisted into the database.
     *
     * @param  flightNumber  flightNumber of the flight
     * @param  price price of the flight
     * @param  from  origin of the flight
     * @param  to to of the flight
     * @param  departureTime departureTime of the flight
     * @param  arrivalTime  arrivalTime of the flight
     * @param  description description of the flight
     * @param  capacity  capacity of the flight
     * @param  model model of the flight
     * @param  manufacturer manufacturer of the flight
     * @param  manufacturedYear year of the flight
     * @return success code status and the updated flight details in xml format
     */
    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFlight(@PathVariable String flightNumber,
                                       @RequestParam("price") int price,
                                       @RequestParam("origin") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("departureTime") String departureTime,
                                       @RequestParam("arrivalTime") String arrivalTime,
                                       @RequestParam("description") String description,
                                       @RequestParam("capacity") int capacity,
                                       @RequestParam("model") String model,
                                       @RequestParam("manufacturer") String manufacturer,
                                       @RequestParam("year") int manufacturedYear){

        System.out.println("--------- updateFlight ----------");
        return flightService.updateFlight(flightNumber, price, from, to,
                departureTime, arrivalTime, capacity, description,model,manufacturer,manufacturedYear);
    }


}
