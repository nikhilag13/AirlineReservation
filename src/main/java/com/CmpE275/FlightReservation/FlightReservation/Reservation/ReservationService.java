package com.CmpE275.FlightReservation.FlightReservation.Reservation;

import com.CmpE275.FlightReservation.FlightReservation.Flight.Flight;
import com.CmpE275.FlightReservation.FlightReservation.Flight.FlightRepository;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.Passenger;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerRepository;
import com.CmpE275.FlightReservation.FlightReservation.Plane.Plane;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {

    @Autowired ReservationRepository reservationRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    PassengerRepository passengerRepository;

    public ResponseEntity<?> getReservation(int number){
        Reservation reservation = reservationRepository.findByReservationNumber(number);
        if(reservation==null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Reservation does not exist"),
                    HttpStatus.NOT_FOUND);
        }else{
            return  new ResponseEntity<>(convertReservationtoJSON(reservation).toString(),HttpStatus.OK);
        }

    }

    public String getErrorMessage(String header, String code, String message){
        JSONObject errorMessage = new JSONObject();
        JSONObject codeandMesaage =  new JSONObject();
        try {
            codeandMesaage.put("code", code);
            codeandMesaage.put("msg",message);
            errorMessage.put(header,codeandMesaage);
        }catch(Exception e){
            System.out.println("getErrorMessage method exception");
        }
        return errorMessage.toString();
    }

    public JSONObject convertReservationtoJSON(Reservation reservation){
        JSONObject result  =  new JSONObject();
        JSONObject res = new JSONObject();
        JSONObject flights = new JSONObject();
        JSONObject passenger = new JSONObject();
        JSONObject flights_array[] =  new JSONObject[reservation.getFlights().size()];

        try{
            passenger.put("id", ""+reservation.getPassenger().getPassengerNumber());
            passenger.put("firstname", reservation.getPassenger().getFirstname());
            passenger.put("lastname", reservation.getPassenger().getLastname());
            passenger.put("age", ""+reservation.getPassenger().getAge());
            passenger.put("gender", reservation.getPassenger().getGender());
            passenger.put("phone", reservation.getPassenger().getPhone());
            int i =0;
            int price=0;
            for(Flight flight : reservation.getFlights()){
                flights_array[i++] =  convertFlightToJSON(flight);
                price += flight.getPrice();
                // to be checked
               // flight.getPassenger().add(passenger);
            }

            flights.put("flight",flights_array);
            res.put("reservationNumber", ""+reservation.getReservationNumber());
            res.put("price",""+price);
            res.put("passenger",passenger);
            res.put("flights",flights);
            result.put("reservation",res);

        }catch(Exception e){
          System.out.println("Exception while converting Reservation to JSON");
        }

      return result;
    }

    public JSONObject convertFlightToJSON(Flight flight){
        JSONObject jsonflight = new JSONObject();
        System.out.println("inside flightToJSONString()");

        try {
            jsonflight.put("number", flight.getFlightNumber());
            jsonflight.put("price", ""+flight.getPrice());
            jsonflight.put("from", flight.getFromPlace());
            jsonflight.put("to", flight.getToPlace());
            jsonflight.put("departureTime", flight.getDepartureTime());
            jsonflight.put("arrivalTime", flight.getArrivalTime());
            jsonflight.put("description", flight.getDescription());
            jsonflight.put("seatsLeft", ""+flight.getSeatsLeft());
            jsonflight.put("plane", convertPlaneToJSON(flight.getPlane()));
        } catch (Exception e) {
            System.out.println("Error while converting Flight to JSON");
        }
        return jsonflight;
    }

    public JSONObject convertPlaneToJSON(Plane plane){
        JSONObject jsonPlane = new JSONObject();

        try {
            jsonPlane.put("capacity", ""+plane.getCapacity());
            jsonPlane.put("model", plane.getModel());
            jsonPlane.put("manufacturer", plane.getManufacturer());
            jsonPlane.put("yearOfManufacture", ""+plane.getYear());
        } catch (Exception e) {
            System.out.println("Error while converting Plane to JSON");
        }
        return jsonPlane;
    }

    public ResponseEntity<?> deleteReservation(int number){
        Reservation reservation = reservationRepository.findByReservationNumber(number);
        if(reservation==null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Reservation does not exist"),
                    HttpStatus.NOT_FOUND);
        }else{
            reservation.getPassenger().getReservations().remove(reservation);
            for(Flight flight : reservation.getFlights()){
                flight.setSeatsLeft(flight.getSeatsLeft()+1);
                flight.getPassengers().remove(reservation.getPassenger());
            }
            reservationRepository.delete(reservation);
            try {
                return new ResponseEntity<>(XML.toString(new JSONObject(getErrorMessage("Response", "200", "Reservation with number " + number + " is canceled successfully"))), HttpStatus.OK);
            }catch (Exception e){
                System.out.println("Error converting json to xml");
                //to be checked
                return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                        "Error converting json to xml"),
                        HttpStatus.NOT_FOUND);
            }
        }

    }

    public ResponseEntity<?> makeReservation(int passengerNumber,List<Flight> flights ){

        Passenger passenger = passengerRepository.findByPassengerNumber(passengerNumber);
        if(passenger == null)
            return new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                    "Passenger does not exist"),
                    HttpStatus.BAD_REQUEST);

        Boolean flightOverlap = checkIfFlightTimingsOverlap(flights);
        if(flightOverlap)
            return new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                    "The Flight timings overlap"),
                    HttpStatus.BAD_REQUEST);
        Boolean passengerOtherFlightsOverlap =  checkIfPassengerOtherFlightsOverlap(passengerNumber,flights);
        if(passengerOtherFlightsOverlap)
            return new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                "The Passenger Other Flight timings overlap"),
                HttpStatus.BAD_REQUEST);

        Boolean flightSeatsUnAvailable= false
                ;
        for(Flight flight : flights){
            if(flight.getSeatsLeft() <= 0)
                flightSeatsUnAvailable = true;
        }

        if(flightSeatsUnAvailable)
            return new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                    "The Flight Seats are unavailable"),
                    HttpStatus.BAD_REQUEST);

     //add the passenger to the flight
        for(Flight flight : flights){
            flight.setSeatsLeft(flight.getSeatsLeft()-1);
        }

        for(Flight flight : flights){
            flight.getPassengers().add(passenger);
        }

        Reservation reservation = new Reservation(passenger, 0,flights);
        passenger.getReservations().add(reservation);

        reservationRepository.save(reservation);
        System.out.println("inside addReservation() if 2");

       try{
           return  new ResponseEntity<>(XML.toString(convertReservationtoJSON(reservation)),HttpStatus.OK);
       }catch (Exception e){
           System.out.println("Error converting json to xml");
           //to be checked
           return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                   "Error converting json to xml"),
                   HttpStatus.NOT_FOUND);

       }


    }

    private Boolean checkIfFlightTimingsOverlap( List<Flight> flights) {
        Boolean result = false;
        for(int i=0;i<flights.size();i++){
            for(int j=i+1;j<flights.size();j++){
                Date currentFlightDepartureDate=flights.get(i).getDepartureTime();
                Date currentFlightArrivalDate=flights.get(i).getArrivalTime();
                Date dep=flights.get(j).getDepartureTime();
                Date arr=flights.get(j).getArrivalTime();
                if((currentFlightArrivalDate.compareTo(dep)>=0 && currentFlightArrivalDate.compareTo(arr)<=0) || (currentFlightDepartureDate.compareTo(dep)>=0 && currentFlightDepartureDate.compareTo(arr)<=0)){
                  result = true;
                  break;

                }
            }
        }
        return result;

    }

    private Boolean checkIfPassengerOtherFlightsOverlap(int passengerNumber, List<Flight> flights){
       Boolean result = false;
        List<Reservation> reservations=passengerRepository.findByPassengerNumber(passengerNumber).getReservations();
        List<Flight> passengerFlights=new ArrayList<Flight>();
        for(Reservation reservation:reservations){
            for(Flight flight:reservation.getFlights()){
                passengerFlights.add(flight);
            }
        }
        for(int i=0;i<flights.size();i++){
            for(int j=0;j<passengerFlights.size();j++){

                Date flightDepartureDate=flights.get(i).getDepartureTime();
                Date flightArrivalDate=flights.get(i).getArrivalTime();
                Date dep=passengerFlights.get(j).getDepartureTime();
                Date arr=passengerFlights.get(j).getArrivalTime();
                if((flightArrivalDate.compareTo(dep)>=0 && flightArrivalDate.compareTo(arr)<=0) || (flightDepartureDate.compareTo(dep)>=0 && flightDepartureDate.compareTo(arr)<=0)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
