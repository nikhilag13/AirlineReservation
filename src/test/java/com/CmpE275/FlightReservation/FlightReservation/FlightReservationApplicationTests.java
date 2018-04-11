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



    @Test
    public void checkgetFlightStatus() throws Exception {
        this.mockMvc.perform(get("/flight/GH2Z4")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void checkInvalidFlight() throws Exception {
        this.mockMvc.perform(get("/flight/gggg")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNewFlight() throws Exception {
        this.mockMvc.perform(post("/flight/GH2Z9?price=100&origin=CA&to=WA&departureTime=2018-04-25-08" +
                "&arrivalTime=2018-04-26-14&description=New-Flight&capacity=120&model=Boeing&manufacturer=Boeing&year=2000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("GH2Z9")));
    }

    @Test
    public void createFlightwithInvalidTime() throws Exception {
        this.mockMvc.perform(post("/flight/GH210?price=100&from=CA&to=WA&departureTime=2018-04-25-08" +
                "&arrivalTime=2018-04-24-14&description=New-Flight&capacity=120&model=Boeing&manufacturer=Boeing&year=1997"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void checkgetPassengerStatus() throws Exception {
        this.mockMvc.perform(get("/passenger/2")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nikhila")));;
    }

    @Test
    public void checkgetReservationStatus() throws Exception {
        this.mockMvc.perform(get("/reservation/6")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void checkforIncorrectpassenger() throws Exception {
        this.mockMvc.perform(get("/passenger/9999")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNewPassenger() throws Exception {
        this.mockMvc.perform(post("/passenger?firstname=Manvitha&lastname=CH&age=25&gender=female&phone=301")).
                andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void checkifPassengerAlreadyExists() throws Exception {
        this.mockMvc.perform(post("/passenger?firstname=Nikhila&lastname=G&age=25&gender=female&phone=124")).
                andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void checkforInvalidReservation() throws Exception {
        this.mockMvc.perform(get("/reservation/9999")).andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNewReservation() throws Exception {
        this.mockMvc.perform(post("/reservation?passengerId=1&flightLists=GH2Z9")).andDo(print())
                .andExpect(status().isOk());
    }

}