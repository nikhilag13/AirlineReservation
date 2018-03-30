package com.CmpE275.FlightReservation.FlightReservation.Flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;

    public void addFlight(String flightNumber, int price, String from, String to, String departureTime,
              String arrivalTime, int seatsLeft, String description){

        if(flightNumber==null) {
            System.out.println("Flight Number is null");
            return;
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");

            Date departure = dateFormat.parse(departureTime);
            Date arrival = dateFormat.parse(arrivalTime);
            Flight flight = new Flight(/*flightNumber, price, from, to, departure, arrival, seatsLeft, description*/);
            flightRepository.save(flight);
        }catch(Exception e){

        }


    }
}
