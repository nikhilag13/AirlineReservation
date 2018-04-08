package com.CmpE275.FlightReservation.FlightReservation.Reservation;

import com.CmpE275.FlightReservation.FlightReservation.Flight.Flight;
import com.CmpE275.FlightReservation.FlightReservation.Flight.FlightRepository;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.Passenger;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerRepository;
import com.CmpE275.FlightReservation.FlightReservation.Plane.Plane;
import org.json.JSONException;
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


//TODO:Manvitha's code

    //helper - List<Reservation> reservations, List<Flight> numList, List<Flight> flight, String source
    public void getStuffDone(String ToOrFromPlace, List<Flight> flightsList, List<Flight> newTempList,List<Reservation> reservations ){

        for(int flightIndex=0;flightIndex<flightsList.size();flightIndex++){
            if(!flightsList.get(flightIndex).getFromPlace().equals(ToOrFromPlace)){
                continue;
            } else{
                newTempList.add(flightsList.get(flightIndex));
            }
        }
        reservations = (List<Reservation>) reservationRepository.findAll();

        for(int reservationIndex=0;reservationIndex< reservations.size();reservationIndex++){
            List<Flight> flightList = reservations.get(reservationIndex).getFlights();
            List<Flight> newList = new ArrayList<Flight>();

            for(int flightNumber=0;flightNumber<flightList.size();flightNumber++){
                Flight singleFlight = flightList.get(flightNumber);
                if(!newTempList.contains(singleFlight)){
                    newList.add(singleFlight);
                }
                else{
                    continue;
                }
            }
            for(int newListIndex=0;newListIndex<newList.size();newListIndex++){
                flightList.remove(newList.get(newListIndex));
            }
            if(flightList.size()==0){
                reservations.remove(reservations.get(reservationIndex));
            }

        }
    }


    public ResponseEntity<?> searchReservation(int passengerId, String fromSource,String toDestination,String flightNumber) throws JSONException {

        boolean isPassengerFound = false, isSourceFound = false, isDestinationFound = false;

        Passenger passenger = null;
        List<Reservation> reservations = new ArrayList<>();
        List<Flight> flights = new ArrayList<>();
        List<Flight> holder = new ArrayList<>();

        if(passengerId != -100){
            isPassengerFound = true;
            passenger = passengerRepository.findByPassengerNumber(passengerId);

            if(passenger != null){
                reservations = passenger.getReservations();
            }
        }

        if(!fromSource.equals("from")) {

            if(isPassengerFound){
                for(Reservation reservation : reservations) {
                    holder = new ArrayList<>();

                    flights = reservation.getFlights();

                    for(Flight flight : flights){

                        if(!flight.getFromPlace().equals(fromSource)){
                            holder.add(flight);
                        }
                    }
                    for(Flight flight2 : holder){
                        flights.remove(flight2);
                    }
                    if(flights.size() == 0)
                        reservations.remove(reservation);
                }
            }
            else{
                getStuffDone(fromSource, (List<Flight>) flightRepository.findAll(), new ArrayList<>(), reservations );
            }

            isSourceFound = true;
        }
        if(!toDestination.equals("to")) {

            holder = new ArrayList<>();

            if((isPassengerFound || isSourceFound) && reservations.size() == 0 ){
                for(Reservation reservation : reservations){
                    flights = reservation.getFlights();
                    for(Flight flight : flights){
                        if(!flight.getToPlace().equals(toDestination))
                            holder.add(flight);
                    }
                    for(Flight flight2 : flights){
                        flights.remove(flight2);
                    }
                    if(flights.size() == 0){
                        reservations.remove(reservation);
                    }
                }
            }
            else{
                List<Flight> AllTheFlights = (List<Flight>) flightRepository.findAll();
                List<Flight> newTempFlightHolder = new ArrayList<>();
                getStuffDone(toDestination, AllTheFlights ,newTempFlightHolder, reservations );
            }
            isDestinationFound = true;
        }
        if(!flightNumber.equals("flightNumber")) {

            if((isSourceFound || isDestinationFound || isPassengerFound) && reservations.size() == 0 ){
                for(Reservation reservation : reservations){
                    flights = reservation.getFlights();
                    for(Flight flight : flights){
                        if(!flight.getFlightNumber().equals(flightNumber)){
                            holder.add(flight);
                        }

                    }
                    for(Flight flight2 : holder){
                        flights.remove(flight2);
                    }
                    if(flights.size() == 0){
                        reservations.remove(reservation);
                    }

                }
            }
            else{
                List<Flight> AllTheFlights = (List<Flight>) flightRepository.findAll();
                List<Flight> newTempFlightHolder = new ArrayList<>();
                getStuffDone(toDestination, AllTheFlights ,newTempFlightHolder, reservations );
            }
        }

        return getReservation(reservations);
    }

    private ResponseEntity<?> getReservation(List<Reservation> searchReservationList) throws JSONException {

        if(searchReservationList.size() == 0 || searchReservationList == null  ){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "200",
                    "Sorry, the requested search criteria doesn't return any reservations." ), HttpStatus.OK);
        }
        String allReservations = "";
        for(Reservation reservation : searchReservationList){
            JSONObject reservationHolder = new JSONObject();
            JSONObject passengerJSON = new JSONObject();
            JSONObject flightsJSON = new JSONObject();
            JSONObject allFlightsInReservation[] = new JSONObject[reservation.getFlights().size()];
            JSONObject response = new JSONObject();
            Passenger passenger = reservation.getPassenger();
            int i = 0;
            double price = 0;
            try {
                response.put("reservation", reservationHolder);
                reservationHolder.put("orderNumber", ""+reservation.getReservationNumber());
                passengerJSON.put("id", ""+passenger.getPassengerNumber());
                passengerJSON.put("FirstName", passenger.getFirstname());
                passengerJSON.put("LastName", passenger.getLastname());
                passengerJSON.put("Age", ""+passenger.getAge());
                passengerJSON.put("Gender", passenger.getGender());
                passengerJSON.put("Phone", passenger.getPhone());
                reservationHolder.put("Passenger", passengerJSON);
                for(Flight flight : reservation.getFlights()){
                    allFlightsInReservation[i++] =  flightToJSONString(flight);
                    price = price + flight.getPrice();
                    flight.getPassengers().add(passenger);
                }
                reservationHolder.put("price", ""+price);
                flightsJSON.put("flight", allFlightsInReservation);
                reservationHolder.put("flights", flightsJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            allReservations += response.toString();
        }

        return  new ResponseEntity<>(XML.toString(new JSONObject(allReservations)), HttpStatus.OK);
    }


    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightJSON = new JSONObject();
        try {
            flightJSON.put("number", flight.getFlightNumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getFromPlace());
            flightJSON.put("to", flight.getFromPlace());
            flightJSON.put("departureTime", flight.getDepartureTime());
            flightJSON.put("arrivalTime", flight.getArrivalTime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", ""+flight.getSeatsLeft());
            flightJSON.put("plane", ConvertPlaneToJSON(flight.getPlane()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flightJSON;
    }

    public JSONObject ConvertPlaneToJSON(Plane plane){
        JSONObject planeJSON = new JSONObject();
        try {
            planeJSON.put("capacity", ""+plane.getCapacity());
            planeJSON.put("model", plane.getModel());
            planeJSON.put("manufacturer", plane.getManufacturer());
            planeJSON.put("yearOfManufacture", ""+plane.getYear());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planeJSON;
    }

    public ResponseEntity<?> updateReservation(int reservationNumber,List<Flight> flightsToAdd,List<Flight> flightsToRemove) throws JSONException {

        //First Remove the FLights
        Reservation reservationToBeUpdated = reservationRepository.findByReservationNumber(reservationNumber);

        if(reservationToBeUpdated == null){
            return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Sorry, the requested reservation with number "
                            + reservationNumber + " does not exist"), HttpStatus.NOT_FOUND);
        }
        for(Flight flight: flightsToRemove){
            reservationToBeUpdated.getFlights().remove(flight);
        }
        reservationRepository.save(reservationToBeUpdated);

        //Now add the flights and check for over-lapping time constraints
        Reservation reservationToAddFlights = reservationRepository.findByReservationNumber(reservationNumber);
        int passengerId = reservationToAddFlights.getPassenger().getPassengerNumber();
        if(!checkIfFlightTimingsOverlap(flightsToAdd) &&
                !checkIfPassengerOtherFlightsOverlap(passengerId, flightsToAdd)){
            for(Flight flight:flightsToAdd){
                reservationToAddFlights.getFlights().add(flight);
            }
            reservationRepository.save(reservationToAddFlights);
            Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber);
            return new ResponseEntity<>(XML.toString(convertReservationtoJSON(reservation)),HttpStatus.OK);
        }
        else{
            Boolean currentReservationFlights=checkIfFlightTimingsOverlap(flightsToAdd);
            Boolean passengerFlights=checkIfPassengerOtherFlightsOverlap(passengerId, flightsToAdd);
            if(currentReservationFlights){
                return new ResponseEntity<>(XML.toString(new JSONObject(getErrorMessage("BadRequest", "404",
                        "Sorry, the timings of flights overlap" ))), HttpStatus.NOT_FOUND);
            }
            if(passengerFlights){
                return new ResponseEntity<>(XML.toString(new JSONObject(getErrorMessage("BadRequest", "404",
                        "Sorry, the timings of flights overlap" ))), HttpStatus.NOT_FOUND);
            }
            return  new ResponseEntity<>(getErrorMessage("Response", "404", "Time Overlap Constraint Violated"),HttpStatus.NOT_FOUND);

        }

    }
}
