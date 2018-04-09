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

    @RequestMapping(value="/passenger/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePassenger(@PathVariable String id) {
        return passengerService.deletePassenger(id);
    }

    @RequestMapping(value="/passenger", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Iterable<Passenger> getAllPassengers() {
        System.out.println("Getting all the passengers");
        return passengerService.getAllPassengers();
    }

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
