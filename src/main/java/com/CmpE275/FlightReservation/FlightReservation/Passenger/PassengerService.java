package com.CmpE275.FlightReservation.FlightReservation.Passenger;


import com.CmpE275.FlightReservation.FlightReservation.Flight.Flight;
import com.CmpE275.FlightReservation.FlightReservation.Plane.Plane;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.Reservation;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.ReservationRepository;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.ReservationService;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.xml.ws.Response;
import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    public Iterable<Passenger> getAllPassengers() {

        return passengerRepository.findAll();
    }

    public ResponseEntity<?> addPassenger(String firstname, String lastname, String age, String gender, String phone) {

        Passenger existingPassenger = passengerRepository.findByPhone(phone);
        JSONObject returnJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        if(existingPassenger != null){
            System.out.println("Passenger already exists!");
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The Passenger Already Exists" ),HttpStatus.NOT_FOUND);
        }
        else{
            System.out.println("Passenger Record is being entered");
            Passenger NewPassengerRecord = new Passenger(firstname, lastname, Integer.parseInt(age), gender, phone);
            passengerRepository.save(NewPassengerRecord);
            System.out.println("User Details Successfully Saved");
            try {
                NewPassengerRecord.setPassengerNumber(NewPassengerRecord.getPassengerNumber());
                returnJSON.put("passenger", innerJSON);
                innerJSON.put("id", NewPassengerRecord.getPassengerNumber());
                innerJSON.put("firstname", firstname);
                innerJSON.put("age", age);
                innerJSON.put("gender", gender);
                innerJSON.put("phone", phone);
                innerJSON.put("lastname", lastname);

                JSONObject reservations = new JSONObject();
                JSONObject reservationArray[] = new JSONObject[0];
                reservations.put("reservation", reservationArray);
                innerJSON.put("reservations", reservations);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(returnJSON.toString(), HttpStatus.CREATED);
        }

    }
    public ResponseEntity<?> updatePassenger(String id, String firstname,String lastname, String age, String gender, String phone) {

        Passenger existingPassenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        JSONObject innerJSON = new JSONObject();

        if(existingPassenger != null) {
            try {
                Passenger PassengerToBeUpdated = passengerRepository.findByPhone(phone);
                if (PassengerToBeUpdated != null && PassengerToBeUpdated.getPassengerNumber() != Integer.parseInt(id))
                    return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The passenger with phone number " + PassengerToBeUpdated.getPhone()
                            + " already exists!"), HttpStatus.NOT_FOUND);

                existingPassenger.setAge(Integer.parseInt(age));
                existingPassenger.setFirstname(firstname);
                existingPassenger.setGender(gender);
                existingPassenger.setLastname(lastname);
                existingPassenger.setPhone(phone);
                passengerRepository.save(existingPassenger);
                innerJSON.put("id", existingPassenger.getPassengerNumber());
                innerJSON.put("firstname", firstname);
                innerJSON.put("lastname", lastname);
                innerJSON.put("age", age);
                innerJSON.put("gender", gender);
                innerJSON.put("phone", phone);
            } catch( Exception e){
                return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The requested passenger was not updated" ),HttpStatus.NOT_FOUND);
            }
        }
        else{

            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The passenger already exists!"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(innerJSON.toString(),HttpStatus.OK);
    }

    public ResponseEntity<?> deletePassenger(String id) {


        Passenger existingPassenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        if(existingPassenger == null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The requested passenger with id " + id +" does not exist" ), HttpStatus.NOT_FOUND);
        }
        else{
            try{
                List<Reservation> reservations = reservationRepository.findByPassenger(existingPassenger);

                for(Reservation reservation : reservations){
                    reservationService.deleteReservation(reservation.getReservationNumber());
                }

                passengerRepository.delete(existingPassenger);
            }
            catch(Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "OOPS! There was some problem deleting the passenger. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return new ResponseEntity<>(getErrorMessage("Response", "200", "Passenger with id " + id + " is deleted successfully"),HttpStatus.OK);
    }

    public String getErrorMessage(String header, String code, String message){
        JSONObject errorMessage = new JSONObject();
        JSONObject errorCodeandMesaage =  new JSONObject();
        try {
            errorCodeandMesaage.put("code", code);
            errorCodeandMesaage.put("msg",message);
            errorMessage.put(header,errorCodeandMesaage);
        }catch(Exception e){
            e.printStackTrace();

        }
        return errorMessage.toString();
    }

    public ResponseEntity<?> getPassenger(String id, boolean isJson) throws JSONException {
        System.out.println("getPassenger()"+isJson);
        Passenger passenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));

        if(passenger == null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "Sorry, the requested passenger with id "
                    + id +" does not exist"), HttpStatus.NOT_FOUND);
        }
        else{
            if(isJson){
                System.out.println("JSON requested");
                return  new ResponseEntity<>(ConvertPassengerToJSONString(passenger),HttpStatus.OK);
            }
            else{
                System.out.println("XML requested");
                return  new ResponseEntity<>(XML.toString(convertPassengerToJSON(passenger)),HttpStatus.OK);
            }
        }
    }

    public JSONObject convertPassengerToJSON(Passenger passenger){
        JSONObject passengerJSON = new JSONObject();
        try{
            passengerJSON.put("id", ""+passenger.getPassengerNumber());
            passengerJSON.put("firstname", ""+passenger.getFirstname());
            passengerJSON.put("lastname", ""+passenger.getLastname());
            passengerJSON.put("age", ""+passenger.getAge());
            passengerJSON.put("gender", ""+passenger.getGender());
            passengerJSON.put("phone", ""+passenger.getPhone());
            passengerJSON.put("reservations", ""+passenger.getPhone());
        }catch (Exception e){
            e.printStackTrace();

        }

        return passengerJSON;

    }

    public String ConvertPassengerToJSONString(Passenger passenger){
        JSONObject result = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject reservationsJSON = new JSONObject();
        JSONObject passengerArray[] = null;
        try {
            System.out.println("inside passengerToJSONString");
            result.put("passenger", fields);
            fields.put("id", ""+passenger.getPassengerNumber());
            fields.put("firstname", passenger.getFirstname());
            fields.put("lastname", passenger.getLastname());
            fields.put("age", ""+passenger.getAge());
            fields.put("gender", passenger.getGender());
            fields.put("phone", passenger.getPhone());

            int index = 0;
            List<Reservation> reservations = passenger.getReservations();
            passengerArray = new JSONObject[reservations.size()];
            for(Reservation reservation : reservations){
                passengerArray[index++] = reservationToJSONString(reservation);
                System.out.println(reservation.getReservationNumber());
                System.out.println(reservation.getPrice());
            }
            reservationsJSON.put("reservation", passengerArray);
            fields.put("reservations", reservationsJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public JSONObject reservationToJSONString(Reservation reservation){

        JSONObject reservationToJSONString = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject reservationArray[] = new JSONObject[reservation.getFlights().size()];
        int index = 0;
        double price = 0;

        try {
            reservationToJSONString.put("orderNumber", ""+reservation.getReservationNumber());

            for(Flight flight : reservation.getFlights()){
                reservationArray[index++] =  flightToJSONString(flight);
                price += flight.getPrice();
            }
            reservationToJSONString.put("price", ""+price);
            flightsJSON.put("flight", reservationArray);
            reservationToJSONString.put("flights", flightsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reservationToJSONString;
    }

    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightToJSONString = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        try {
            flightToJSONString.put("flight", flightJSON);
            flightJSON.put("number", flight.getFlightNumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getFromPlace());
            flightJSON.put("to", flight.getFromPlace());
            flightJSON.put("departureTime", flight.getDepartureTime());
            flightJSON.put("arrivalTime", flight.getArrivalTime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flightToJSONString;
    }


    public JSONObject planeToJSONString(Plane plane){
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

}
