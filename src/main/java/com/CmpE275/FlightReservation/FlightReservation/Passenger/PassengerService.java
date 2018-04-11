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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    /**
     * Returns an HTTP response entity which is the passenger
     * details. The passenger details are passed from client and are
     * persisted into the database.
     *
     * @param  firstname  firstname of the passenger
     * @param  lastname lastname of the passenger
     * @param  age age of the passenger
     * @param  gender gender of the passenger
     * @param  phone phone of the passenger
     * @return code status and the newly created passenger details in JSON format or error message
     */
    public ResponseEntity<?> addPassenger(String firstname, String lastname, String age, String gender, String phone) {

        Passenger existingPassenger = passengerRepository.findByPhone(phone);
        JSONObject returnJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        if(existingPassenger != null){
            System.out.println("Passenger already exists!");
            return new ResponseEntity<>(getErrorMessage("BadRequest", "400", "The Passenger Already Exists" ),HttpStatus.BAD_REQUEST);
        }
        else{
            System.out.println("Passenger Record is being entered");
            Passenger NewPassengerRecord = new Passenger(firstname, lastname, Integer.parseInt(age), gender, phone);
            passengerRepository.save(NewPassengerRecord);
            System.out.println("User Details Successfully Saved");
            try {
                NewPassengerRecord.setPassengerNumber(NewPassengerRecord.getPassengerNumber());
                innerJSON.put("id", NewPassengerRecord.getPassengerNumber());
                returnJSON.put("passenger", innerJSON);
                innerJSON.put("firstname", firstname);
                innerJSON.put("lastname", lastname);
                innerJSON.put("age", age);
                innerJSON.put("gender", gender);
                innerJSON.put("phone", phone);
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

    /**
     * Returns an HTTP response entity which is the passenger
     * details. The passenger details are passed from client and are
     * updated and persisted into the database.
     *
     * @param  firstname  firstname of the passenger
     * @param  lastname lastname of the passenger
     * @param  age age of the passenger
     * @param  gender gender of the passenger
     * @param  phone phone of the passenger
     * @return code status and the updated passenger details in JSON format or error message
     */
    public ResponseEntity<?> updatePassenger(String id, String firstname,String lastname, String age, String gender, String phone) {

        Passenger existingPassenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        JSONObject innerJSON = new JSONObject();

        if(existingPassenger != null) {
            try {
                Passenger PassengerToBeUpdated = passengerRepository.findByPhone(phone);
                if (PassengerToBeUpdated != null && PassengerToBeUpdated.getPassengerNumber() != Integer.parseInt(id))
                    return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The Phone number of " + PassengerToBeUpdated.getPhone()
                            + " cannot be updated!"), HttpStatus.NOT_FOUND);

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
                JSONObject passengerArray[] = null;
                int index = 0;
                List<Reservation> reservations = existingPassenger.getReservations();
                passengerArray = new JSONObject[reservations.size()];
                for(Reservation reservation : reservations){
                    passengerArray[index++] = reservationToJSONString(reservation);
                    System.out.println(reservation.getReservationNumber());
                    System.out.println(reservation.getPrice());
                }
                JSONObject reservationsJSON = new JSONObject();
                reservationsJSON.put("reservation", passengerArray);
                innerJSON.put("reservations", reservationsJSON);

            } catch( Exception e){
                return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The requested passenger was not updated" ),HttpStatus.NOT_FOUND);
            }
        }
        else{

            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The passenger Number does not exist! Please try with valid number."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(innerJSON.toString(),HttpStatus.OK);
    }

    /**
     * Deletes the passenger data from the database
     * passenger id is passed from the client
     *
     * @param  id  Unique id of the passenger
     * @return  code status and the confirmation or error message
     */
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

    /**
     * If any error occurs in the processing of the request
     * an error message is sent in the form of JSON response
     *
     * @param  header  header of the http request
     * @param  code  code status in the http request
     * @param  message  message of the http request
     * @return  error message object
     */
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

    /**
     * Retrives the details of the passenger
     * based on the unique id. If no valid passenger id is passed,
     * returns an error msg.
     *
     * @param  id  Unique id of the passenger
     * @return  code status and the passenger entity or error message
     */
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
                return  new ResponseEntity<>(ConvertPassengerToJSONString(passenger).toString(),HttpStatus.OK);
            }
            else{
                System.out.println("XML requested");
                return  new ResponseEntity<>(XML.toString(ConvertPassengerToJSONString(passenger)),HttpStatus.OK);
            }
        }
    }

    /**
     * Converts the passenger details retrieved from the
     * repository and convert into JSON Object for the
     * request
     *
     * @param  passenger  Passenger Object
     * @return  passenger JSON Object to be sent
     */
    public JSONObject ConvertPassengerToJSONString(Passenger passenger){
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

        return result;
    }


    /**
     * Converts the reservation details retrieved from the
     * repository and convert into JSON Object for the
     * request
     *
     * @param  reservation  reservation Object
     * @return  reservation JSON Object to be sent
     */
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

    /**
     * Converts the flight details retrieved from the
     * repository and convert into JSON Object for the
     * request
     *
     * @param  flight  flight Object
     * @return  flight JSON Object to be sent
     */
    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightToJSONString = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        try {
            flightToJSONString.put("flight", flightJSON);
            flightJSON.put("number", flight.getFlightNumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getFromPlace());
            flightJSON.put("to", flight.getFromPlace());
            flightJSON.put("departureTime", dateFormat.format(flight.getDepartureTime()));
            flightJSON.put("arrivalTime", dateFormat.format(flight.getArrivalTime()));
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flightToJSONString;
    }

    /**
     * Converts the plane details retrieved from the
     * repository and convert into JSON Object for the
     * request
     *
     * @param  plane  plane Object
     * @return  plane JSON Object to be sent
     */
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
