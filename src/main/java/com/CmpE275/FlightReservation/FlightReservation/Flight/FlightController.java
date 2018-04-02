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

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST)
    public ResponseEntity<?> addFlight(@PathVariable String flightNumber,
                                       @RequestParam("price") int price,
                                       @RequestParam("from") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("departureTime") String departureTime,
                                       @RequestParam("arrivalTime") String arrivalTime,
                                       @RequestParam("description") String description,
                                       @RequestParam("capacity") int capacity,
                                       @RequestParam("model") String model,
                                       @RequestParam("manufacturer") String manufacturer,
                                       @RequestParam("manufacturedYear") int manufacturedYear){

        System.out.println("--------- addNewFlight ----------");
         return flightService.addNewFlight(flightNumber, price, from, to,
                departureTime, arrivalTime, capacity, description,model,manufacturer,manufacturedYear);
    }

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFlight(@PathVariable String flightNumber){

        System.out.println("--------- deleteFlight ----------");
        return flightService.deleteFlight(flightNumber);

    }

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.GET)
    public ResponseEntity<?> getFlight(@PathVariable String flightNumber, @RequestParam(value = "xml", required = false) boolean responseinXml){
        System.out.println("--------- getFlight ----------");
        return flightService.getFlightResponse(flightNumber,responseinXml);

    }
    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFlight(@PathVariable String flightNumber,
                                       @RequestParam("price") int price,
                                       @RequestParam("from") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("departureTime") String departureTime,
                                       @RequestParam("arrivalTime") String arrivalTime,
                                       @RequestParam("description") String description,
                                       @RequestParam("capacity") int capacity,
                                       @RequestParam("model") String model,
                                       @RequestParam("manufacturer") String manufacturer,
                                       @RequestParam("manufacturedYear") int manufacturedYear){

        System.out.println("--------- updateFlight ----------");
        return flightService.updateFlight(flightNumber, price, from, to,
                departureTime, arrivalTime, capacity, description,model,manufacturer,manufacturedYear);
    }


}
