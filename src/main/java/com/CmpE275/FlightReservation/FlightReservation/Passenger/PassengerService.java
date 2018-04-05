package com.CmpE275.FlightReservation.FlightReservation.Passenger;


import net.minidev.json.JSONObject;
//import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.xml.ws.Response;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public Iterable<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }


    public ResponseEntity<?> addPassenger(String firstname, String lastname, String age, String gender, String phone) {

        System.out.println("In add passenger - service");
        System.out.println("Add passenger details - "+firstname+' '+lastname+' '+age+' '+gender+' '+phone);
        Passenger existingPassenger = passengerRepository.findByPhone(phone);
        System.out.println("Found?");
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


                //TODO:reservations.put("reservation", arr);
                //TODO:json.put("reservations", reservations);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(returnJSON.toString(), HttpStatus.CREATED);
        }

    }
    public ResponseEntity<?> updatePassenger(String id, String firstname,String lastname, String age, String gender, String phone) {
        System.out.println("Update Passenger details - Controller");

        Passenger existingPassenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        JSONObject innerJSON = new JSONObject();

        if(existingPassenger != null) {
            System.out.println("Updating the passenger details");
            try {
//                Passenger PassengerToBeUpdated = passengerRepository.findByPhone(phone);
//                if (PassengerToBeUpdated != null && PassengerToBeUpdated.getPassengerId() != id)
//                    return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The passenger with phone number " + PassengerToBeUpdated.getPhone()
//                            + " already exists!"), HttpStatus.NOT_FOUND);

                existingPassenger.setAge(Integer.parseInt(age));
                existingPassenger.setFirstname(firstname);
                existingPassenger.setGender(gender);
                existingPassenger.setLastname(lastname);
                existingPassenger.setPhone(phone);
                passengerRepository.save(existingPassenger);
                System.out.println("Successfully updated the passenger details");
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

            System.out.println("Passenger details cannot be updated as the passenger does not exist");
        }

        return new ResponseEntity<>(innerJSON.toString(),HttpStatus.OK);
    }

    public ResponseEntity<?> deletePassenger(String id) {

        System.out.println("Delete Passenger - Service");

        Passenger existingPassenger = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        if(existingPassenger == null){
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The requested passenger with id " + id +" does not exist" ), HttpStatus.NOT_FOUND);
        }
        else{
            try{
                //TODO:List<Reservation> passengerReservations = reservationRepository.findByPassenger(passenger);
//                for(Reservation reservation : reservations){
//                    deleteReservation(reservation, passenger);
//                }
                //throw new RuntimeException("Testing Transections");

                passengerRepository.delete(existingPassenger);
            }
            catch(Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "OOPS! There was some problem deleting the passenger. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        //TODO:Return in XML Format
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
            System.out.println("getErrorMessage method exception");

        }
        return errorMessage.toString();
    }

    public ResponseEntity<?> getPassenger(String id, boolean isJson){
        System.out.println("in Get Passenger - Service");

        Passenger passengerInfo = passengerRepository.findByPassengerNumber(Integer.parseInt(id));
        if(passengerInfo == null){
            System.out.println("The Requested Passenger does not exist");
            return new ResponseEntity<>(getErrorMessage("BadRequest", "404", "The requested passenger with id "
                    + id +" does not exist"), HttpStatus.NOT_FOUND);
        }
        else{
            if(isJson) {
                return new ResponseEntity<>(convertPassengerToJSON(passengerInfo), HttpStatus.OK);
            } else{

                //return  new ResponseEntity<>(XML.toString(new JSONObject(convertPassengerToJSON(passengerToBeDeleted))),HttpStatus.OK);
                return null;
            }


        }
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
