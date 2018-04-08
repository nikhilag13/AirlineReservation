package com.CmpE275.FlightReservation.FlightReservation.Passenger;


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
        System.out.println("getPassenger()");
        Passenger passenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));

        if(passenger == null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "Sorry, the requested passenger with id "
                    + id +" does not exist"), HttpStatus.NOT_FOUND);
        }
        else{
            if(isJson)
                return  new ResponseEntity<>(convertPassengerToJSON(passenger),HttpStatus.OK);
            else
                return  new ResponseEntity<>(XML.toString(new JSONObject(convertPassengerToJSON(passenger))),HttpStatus.OK);
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
        }catch (Exception e){
            e.printStackTrace();

        }

        return passengerJSON;

    }
}
