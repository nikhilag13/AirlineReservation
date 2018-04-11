package com.CmpE275.FlightReservation.FlightReservation.Passenger;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@Transactional
public class PassengerController {

    @Autowired
    public PassengerService passengerService;

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
    @RequestMapping(value="/passenger", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPassenger(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("age") String age,
            @RequestParam("gender") String gender,
            @RequestParam("phone") String phone
    ) {
        System.out.println("In add passenger - Controller");
        return passengerService.addPassenger(firstname, lastname, age, gender, phone);
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
    @RequestMapping(value="/passenger/{id}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassenger(
            @PathVariable String id,
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("age") String age,
            @RequestParam("gender") String gender,
            @RequestParam("phone") String phone
    ) {

        return passengerService.updatePassenger(id, firstname, lastname, age, gender, phone);
    }


    /**
     * Deletes the passenger data from the database
     * passenger id is passed from the client
     *
     * @param  id  Unique id of the passenger
     * @return  code status and the confirmation or error message
     */
    @RequestMapping(value="/passenger/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePassenger(
            @PathVariable String id) {
        return passengerService.deletePassenger(id);
    }

    /**
     * Gets the details of all the passengers
     * from the database
     *
     * @return code status and the list of all the passenger details or error message
     */
    @RequestMapping(value="/passenger", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Iterable<Passenger> getAllPassengers() {
        System.out.println("Getting all the passengers");
        return passengerService.getAllPassengers();
    }

    /**
     * Retrives the details of the passenger
     * based on the unique id. If no valid passenger id is passed,
     * returns an error msg.
     *
     * @param  id  Unique id of the passenger
     * @return  code status and the passenger entity or error message
     */
    @RequestMapping(value="/passenger/{id}", method=RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPassenger(
            @PathVariable String id,
            @RequestParam(value = "xml", required=false) String xml) throws JSONException {

        //Checks if the response type to be returned is JSOn or XML
        boolean isJSON = true;

        if(xml != null && xml.equals("true")){
            isJSON = false;
        }
        return passengerService.getPassenger(id, isJSON);
    }


}
