package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        ticket = new Ticket();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        // check if a ticket that is saved in DB and Parking table is updated with availability
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);       
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  63 * 60 * 1000) );
        Date outTime = new Date();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEFG");
        if(ticketDAO.saveTicket(ticket)) {
        	assert(!parkingSpot.isAvailable());
        }
        else
        {
        	//get ticket from DB
        	Ticket ticketFromDB = ticketDAO.getTicket("ABCDEFG");
        	//reduce the range from milliseconds to min because DB round them to second then compare the two ticket
        	assert(ticket.getInTime().getTime()/60000 == ticketFromDB.getInTime().getTime()/60000);
        	// use getNextAvailableSlot from parkingSpotDAO to check if parkingSpot has been updated in DB
        	assert(parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType()) > parkingSpot.getId());
        }
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEFG");
        double priceFromDB = ticket.getPrice();
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(ticket);
        assert(ticket.getPrice() == priceFromDB);
    }

	@Test
	public void welcomeBack() {
		 ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	        parkingService.processIncomingVehicle();
	        // check if a ticket that is saved in DB and Parking table is updated with availability
	        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);       
	        Date inTime = new Date();
	        inTime.setTime( System.currentTimeMillis() - (  63 * 60 * 1000) );
	        Date outTime = new Date();
	        ticket.setInTime(inTime);
	        ticket.setOutTime(outTime);
	        ticket.setParkingSpot(parkingSpot);
	        fareCalculatorService.calculateFare(ticket);
	        ticket.setParkingSpot(parkingSpot);
	        ticket.setVehicleRegNumber("ABCDEFG");
	        if(ticketDAO.saveTicket(ticket)) {
	        	assert(!parkingSpot.isAvailable());
	        }
	        else
	        {
	        	final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        	final PrintStream originalOut = System.out;
	        	System.setOut(new PrintStream(outContent));
	        	parkingService.processIncomingVehicle();
	        	String welcomeMessage = outContent.toString();
	        	assert(welcomeMessage.contains("Welcome back!"));
	        	System.setOut(originalOut);
	        }
	}
	
	@Test
	public void notWelcomeBack() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
       	final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
       	final PrintStream originalOut = System.out;
	    System.setOut(new PrintStream(outContent));
	    parkingService.processIncomingVehicle();
	    String welcomeMessage = outContent.toString();
	    assert(!welcomeMessage.contains("Welcome back!"));
	    System.setOut(originalOut);
	        
	}
}
