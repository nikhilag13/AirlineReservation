package com.CmpE275.FlightReservation.FlightReservation;

import com.CmpE275.FlightReservation.FlightReservation.Flight.FlightController;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerController;
import com.CmpE275.FlightReservation.FlightReservation.Reservation.ReservationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FlightReservationApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Returns OK for correct Flight ID.
     *
     * @throws Exception the exception
     */

    @Test
    public void checkgetFlightStatus() throws Exception {
        this.mockMvc.perform(get("/flight/GH2Z4")).andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Returns 404 for incorrect Flight ID.
     *
     * @throws Exception the exception
     */


    @Test
    public void checkInvalidFlight() throws Exception {
        this.mockMvc.perform(get("/flight/gggg")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Returns 200 OK after sucessfully creating a new flight.
     *
     * @throws Exception the exception
     */
    @Test
    public void createNewFlight() throws Exception {
        this.mockMvc.perform(post("/flight/GH2Z12?price=100&origin=CA&to=WA&departureTime=2018-04-25-08" +
                "&arrivalTime=2018-04-26-14&description=New-Flight&capacity=120&model=Boeing&manufacturer=Boeing&year=2000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("GH2Z12")));
    }

    /**
     * Returns 404 error when trying to add flight with ivalid depature and arrival times.
     *
     * @throws Exception the exception
     */

    @Test
    public void createFlightwithInvalidTime() throws Exception {
        this.mockMvc.perform(post("/flight/GH210?price=100&from=CA&to=WA&departureTime=2018-04-25-08" +
                "&arrivalTime=2018-04-24-14&description=New-Flight&capacity=120&model=Boeing&manufacturer=Boeing&year=1997"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Returns 200 when trying to fetch details of passenger.
     *
     * @throws Exception the exception
     */


    @Test
    public void checkgetPassengerStatus() throws Exception {
        this.mockMvc.perform(get("/passenger/2")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nikhila")));;
    }

    /**
     * Returns 200 when trying to fetch details of reservation.
     *
     * @throws Exception the exception
     */

    @Test
    public void checkgetReservationStatus() throws Exception {
        this.mockMvc.perform(get("/reservation/6")).andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Returns 400 when trying to fetch details of invalid passenger.
     *
     * @throws Exception the exception
     */

    @Test
    public void checkforIncorrectpassenger() throws Exception {
        this.mockMvc.perform(get("/passenger/9999")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Returns 200 when trying to create a new passenger.
     *
     * @throws Exception the exception
     */
    @Test
    public void createNewPassenger() throws Exception {
        this.mockMvc.perform(post("/passenger?firstname=Manvitha&lastname=CH&age=25&gender=female&phone=303")).
                andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * Returns 400 when trying to creating a passenegr if the passenger already exists.
     *
     * @throws Exception the exception
     */
    @Test
    public void checkifPassengerAlreadyExists() throws Exception {
        this.mockMvc.perform(post("/passenger?firstname=Nikhila&lastname=G&age=25&gender=female&phone=124")).
                andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Returns 400 when trying to fetch details of invalid reservation.
     *
     * @throws Exception the exception
     */
    @Test
    public void checkforInvalidReservation() throws Exception {
        this.mockMvc.perform(get("/reservation/9999")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Returns 200 ok when trying tocreate a new reservation.
     *
     * @throws Exception the exception
     */
    @Test
    public void createNewReservation() throws Exception {
        this.mockMvc.perform(post("/reservation?passengerId=4&flightLists=GH2Z9")).andDo(print())
                .andExpect(status().isOk());
    }

}