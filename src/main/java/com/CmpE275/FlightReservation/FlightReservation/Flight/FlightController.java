package com.CmpE275.FlightReservation.FlightReservation.Flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@Transactional
public class FlightController {
    @Autowired
    private FlightService flightService;

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST)
    public void addFlight(@PathVariable String flightNumber,
                                       @RequestParam("price") int price,
                                       @RequestParam("from") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("departureTime") String departureTime,
                                       @RequestParam("arrivalTime") String arrivalTime,
                                       @RequestParam("description") String description,
                                       @RequestParam("capacity") int capacity,
                                       @RequestParam("model") String model,
                                       @RequestParam("yearOfManufacture") int yearOfManufacture,
                                       @RequestParam("manufacturer") String manufacturer){

        System.out.println("addFlight()#################");
         flightService.addFlight(flightNumber, price, from, to,
                departureTime, arrivalTime, 100, description);
    }
}
