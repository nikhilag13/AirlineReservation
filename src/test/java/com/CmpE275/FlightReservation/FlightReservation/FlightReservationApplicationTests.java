package com.CmpE275.FlightReservation.FlightReservation;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerController;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerRepository;
import com.CmpE275.FlightReservation.FlightReservation.Passenger.PassengerService;
import org.mockito.Mockito;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FlightReservationApplicationTests {

//	@Autowired
//	private PassengerController passengerController;
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	private PassengerRepository passengerRepository;
//
//	@Autowired
//	private PassengerService passengerService;
//
//	@Test
//	public void controllerTest() throws Exception {
//		assertThat(PassengerController).isNotNull();
//	}
//
//	@Test
//	public void shouldReturnAllThePassengers() throws Exception {
//		this.mockMvc.perform(get("/passenger/2")).andExpect(status().isOk());
//	}

}
