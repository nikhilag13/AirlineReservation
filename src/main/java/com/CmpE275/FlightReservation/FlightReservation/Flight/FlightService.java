package com.CmpE275.FlightReservation.FlightReservation.Flight;

import com.CmpE275.FlightReservation.FlightReservation.Passenger.Passenger;
import com.CmpE275.FlightReservation.FlightReservation.Plane.Plane;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.Reservation;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.ReservationRepository;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private  ReservationRepository reservationRepository;

    public ResponseEntity<?> addNewFlight(String flightNumber, int price, String from, String to, String departureTime,
                                          String arrivalTime, int capacity, String description, String model, String manufacturer, int manufacturererYear){

        if(flightNumber==null) {
            System.out.println("Flight Number is null");
            return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "The Flight number is null"),
                    HttpStatus.NOT_FOUND);
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
            Date departureDate = dateFormat.parse(departureTime);
            Date arrivalDate = dateFormat.parse(arrivalTime);

            if (departureDate.compareTo(arrivalDate)>0){
                return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                        "Departure time is greater than arrivalTime"),
                        HttpStatus.NOT_FOUND);

            }
            if(flightRepository.findByFlightNumber(flightNumber)!=null) {
                System.out.println("Flight Number already exists - Update Flight");
                return modifyFlight(flightNumber, price, from, to, departureDate, arrivalDate, capacity, description, model, manufacturer, manufacturererYear);
            }

            System.out.println("New Flight Received");
            Plane p =  new Plane(capacity,model,manufacturer,manufacturererYear);
            Flight flight = new Flight(flightNumber,price,from,to,departureDate,arrivalDate,capacity,description,p,new ArrayList<Passenger>());
            flightRepository.save(flight);
        }catch(Exception e){
            return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Exception during registering Flight"),
                    HttpStatus.NOT_FOUND);

        }
         return getFlightResponse(flightNumber,true);


    }

    public ResponseEntity<?> updateFlight(String flightNumber, int price, String from, String to, String departure,
                                          String arrival, int capacity, String description, String model, String manufacturer, int manufacturererYear) {
        if (flightNumber == null) {
            System.out.println("Flight Number is null");
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "The Flight number is null"),
                    HttpStatus.NOT_FOUND);
        }
        //To do check for flight capacity
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
            Date departureDate = dateFormat.parse(departure);
            Date arrivalDate = dateFormat.parse(arrival);

            if (departureDate.compareTo(arrivalDate) > 0) {
                return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                        "Departure time is greater than arrivalTime"),
                        HttpStatus.NOT_FOUND);
            }
            if (flightRepository.findByFlightNumber(flightNumber) != null) {
                Flight f1 = flightRepository.findByFlightNumber(flightNumber);
                int activeresrevation = f1.getPlane().getCapacity() - f1.getSeatsLeft();
                if(activeresrevation>capacity)
                    return new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                            "Active reservation count for this flight is higher than the target capacity"),
                            HttpStatus.BAD_REQUEST);

                return modifyFlight(flightNumber, price, from, to, departureDate, arrivalDate, capacity, description, model, manufacturer, manufacturererYear);
            }else return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "The Flight is null"),
                    HttpStatus.NOT_FOUND);


        }catch (Exception e) {
            return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Exception during registering Flight"),
                    HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> modifyFlight(String flightNumber, int price, String from, String to, Date departureDate,
                                          Date arrivalDate, int capacity, String description, String model, String manufacturer, int manufacturererYear){

       // System.out.println("modify Flight");
      Flight f = flightRepository.findByFlightNumber(flightNumber);
      if(f!=null){
          if(departureDate.compareTo(f.getDepartureTime())!=0 || arrivalDate.compareTo(f.getArrivalTime())!=0){
              List<Passenger> passengers = f.getPassengers();
              for(Passenger p: passengers){
                  List<Reservation> reservations = p.getReservations();
                  List<Flight> passengerFlights = new ArrayList<Flight>();
                  for(Reservation r: reservations){
                      for(Flight resF: r.getFlights())
                          passengerFlights.add(resF);

                  }
                  f.setDepartureTime(departureDate);
                  f.setArrivalTime(arrivalDate);
                  Boolean overlap = checkforOverlapTime(passengerFlights,f);
                  if(overlap)
                      return  new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                              "This Flight cannot be updated since its time is overlapped with other flights of passenger"),
                              HttpStatus.BAD_REQUEST);

              }

          }

          if(f.getPlane().getCapacity()!= capacity){
              int passengerCount = f.getPlane().getCapacity() - f.getSeatsLeft();
              if(passengerCount>capacity)
                  return  new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                          "This Flight cannot be updated since requested Capacity is greater than Passenger count"),
                          HttpStatus.BAD_REQUEST);
              f.setSeatsLeft(capacity-passengerCount);
          }

          f.setPrice(price);
          f.setFromPlace(from);
          f.setToPlace(to);
          f.setDepartureTime(departureDate);
          f.setArrivalTime(arrivalDate);
          f.setDescription(description);
          f.getPlane().setCapacity(capacity);
          f.getPlane().setManufacturer(manufacturer);
          f.getPlane().setModel(model);
          f.getPlane().setYear(manufacturererYear);;
          flightRepository.save(f);

         return getFlightResponse(flightNumber,true);

      }else{
          return new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                  "The Flight number is null"),
                  HttpStatus.NOT_FOUND);

      }

    }

    public Boolean checkforOverlapTime(List<Flight> passengerFlights, Flight currentFlight){
        Boolean overlap = false;
        for(Flight f: passengerFlights){
            if(f.getFlightNumber().equals(currentFlight.getFlightNumber()))
                continue;
            if((f.getArrivalTime().compareTo(currentFlight.getDepartureTime())>=0 && f.getArrivalTime().compareTo(currentFlight.getArrivalTime())<=0) ||
                     (f.getDepartureTime().compareTo(currentFlight.getDepartureTime())>=0 && f.getDepartureTime().compareTo(currentFlight.getArrivalTime())<=0)){
                overlap = true;
            }

        }
        return overlap;
    }

    public String getErrorMessage(String header, String code, String message){
        JSONObject errorMessage = new JSONObject();
        JSONObject errorCodeandMesaage =  new JSONObject();
        try {
            errorCodeandMesaage.put("code", code);
            errorCodeandMesaage.put("msg",message);
            errorMessage.put(header,errorCodeandMesaage);
        }catch(Exception e){
            System.out.println("getErrorMessage method exception");

        }
        return errorMessage.toString();


    }

    public ResponseEntity<?> getFlightResponse(String flightNumber,Boolean xml){
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if(flight!=null){
            if(!xml)
                return new ResponseEntity<>(convertFlightToJSON(flight).toString(),HttpStatus.OK);
            else {
                try {
                    return new ResponseEntity<>(XML.toString(convertFlightToJSON(flight)), HttpStatus.OK);
                }catch (Exception e){
                    System.out.println("Error converting json to xml");
                    //to be checked
                    return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                            "Error converting json to xml"),
                            HttpStatus.NOT_FOUND);
                }
            }
        }else{

                return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                        "Flight does not exist"),
                        HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteFlight(String flightNumber){
        Flight f = flightRepository.findByFlightNumber(flightNumber);
        if(f!=null){
            List<Reservation> res = (List<Reservation>) reservationRepository.findAll();
            for(Reservation r: res){
                if(r.getFlights().contains(f)){
                    return  new ResponseEntity<>(getErrorMessage("BadRequest", "400",
                            "This Flight has Reservation! Cannot be cancelled"),
                            HttpStatus.BAD_REQUEST);

                }
            }
            try {
                flightRepository.delete(f);
                JSONObject result = new JSONObject();
                JSONObject codeandMessage = new JSONObject();
                codeandMessage.put("code", 200);
                codeandMessage.put("msg","Flight with number "+ flightNumber+" is deleted successfully  ");
                result.put("Response",codeandMessage);

                return new ResponseEntity<>(XML.toString(result),HttpStatus.OK);


            }catch(Exception e){
                return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                        "Flight Could not be deleted due to Exception"),
                        HttpStatus.NOT_FOUND);

            }

        }else{
            return  new ResponseEntity<>(getErrorMessage("BadRequest", "404",
                    "Flight does not exist"),
                    HttpStatus.NOT_FOUND);
        }

    }

    public JSONObject convertFlightToJSON(Flight f){
        JSONObject res = new JSONObject();
        JSONObject jsonFlight = new JSONObject();
        JSONObject plane = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        try{
            jsonFlight.put("flightNumber", f.getFlightNumber());
            jsonFlight.put("price", Double.toString(f.getPrice()));
            jsonFlight.put("from", f.getFromPlace());
            jsonFlight.put("to", f.getToPlace());
            jsonFlight.put("departureTime", dateFormat.format(f.getDepartureTime()));
            jsonFlight.put("arrivalTime", dateFormat.format(f.getArrivalTime()));
            jsonFlight.put("description", f.getDescription());
            jsonFlight.put("seatsLeft", ""+f.getSeatsLeft());
            Plane p = f.getPlane();
            plane.put("capacity", ""+p.getCapacity());
            plane.put("model", p.getModel());
            plane.put("manufacturer", p.getManufacturer());
            plane.put("yearOfManufacture", ""+p.getYear());
            jsonFlight.put("plane", plane);

            JSONObject passengers[] =  new JSONObject[f.getPassengers().size()];
            int i=0;
            for(Passenger pass : f.getPassengers()){
                passengers[i]= convertPassengerToJSON(pass);
                i++;
            }
            JSONObject passenger = new JSONObject();
            passenger.put("passenger",passengers);

            jsonFlight.put("passengers", passenger);

            res.put("flight",jsonFlight);
        }catch (Exception e){
            System.out.println("Exception while converting Flight to JSON");
        }

      return res;

    }

    public JSONObject convertPassengerToJSON(Passenger p){
        JSONObject res = new JSONObject();

        try{
            res.put("id", ""+p.getPassengerNumber());
            res.put("firstname", ""+p.getFirstname());
            res.put("lastname", ""+p.getLastname());
            res.put("age", ""+p.getAge());
            res.put("gender", ""+p.getGender());
            res.put("phone", ""+p.getPhone());
        }catch (Exception e){
            System.out.println("Exception while converting Passenger to JSON");

        }

     return res;

    }


}
