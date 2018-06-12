package com.ticket.service;

import com.ticket.service.interfaces.SeatHold;
import com.ticket.service.interfaces.TicketService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TicketServiceTest {
    TicketService ticketService;

    @Before
    public void setUp() throws RuntimeException {
        //One sec hold expiration for testing
        ticketService = TicketServiceFactory.create(10, 10, 1);
    }

    @Test
    public void testInvalidVenueSizes() throws RuntimeException {
        try {
            ticketService = TicketServiceFactory.create(0, 10, 1);
            fail("Ticket Service cant not have 0 rows");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Rows and Columns cannot be zero!");
        }

        try {
            ticketService = TicketServiceFactory.create(10, 0, 1);
            fail("Ticket Service cant not have 0 cols");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Rows and Columns cannot be zero!");
        }
    }

    @Test
    public void testNumSeatsAvailable() throws RuntimeException {
        assertEquals(ticketService.numSeatsAvailable(), 100);
    }

    @Test
    public void testRequestTooManySeats() {
        SeatHold seatHold = ticketService.findAndHoldSeats(101, "abc@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 0);
    }

    @Test
    public void testFindAndHoldSeats() throws Exception {
        SeatHold seatHold = ticketService.findAndHoldSeats(10, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 10);
        assertEquals(ticketService.numSeatsAvailable(), 90);
        assertEquals(seatHold.displaySeatsOnHold(), "A:1, A:2, A:3, A:4, A:5, A:6, A:7, A:8, A:9, A:10");
        seatHold = ticketService.findAndHoldSeats(7, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 7);
        assertEquals(ticketService.numSeatsAvailable(), 83);
        assertEquals(seatHold.displaySeatsOnHold(), "B:2, B:3, B:4, B:5, B:6, B:7, B:8");
        seatHold = ticketService.findAndHoldSeats(3, "555@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 3);
        assertEquals(ticketService.numSeatsAvailable(), 80);
        assertEquals(seatHold.displaySeatsOnHold(), "B:1, B:9, B:10");
        Thread.sleep(2000);
        seatHold = ticketService.findAndHoldSeats(5, "434@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 5);
        assertEquals(ticketService.numSeatsAvailable(), 95);
        assertEquals(seatHold.displaySeatsOnHold(), "A:3, A:4, A:5, A:6, A:7");
    }

    @Test
    public void testReserveSeats() {
        SeatHold seatHold = ticketService.findAndHoldSeats(5, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 5);
        assertEquals(ticketService.numSeatsAvailable(), 95);
        assertEquals(seatHold.displaySeatsOnHold(), "A:3, A:4, A:5, A:6, A:7");
        assertTrue(ticketService.reserveSeats(seatHold.getId(), seatHold.getEmail()).contains("Reservation code"));
        //Test double confirm reserve
        assertEquals(ticketService.reserveSeats(seatHold.getId(), seatHold.getEmail()), "Reservation code has already been given.");
    }

    @Test
    public void testReserveSeatsBadHoldSeatId() {
        SeatHold seatHold = ticketService.findAndHoldSeats(7, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 7);
        assertEquals(ticketService.numSeatsAvailable(), 93);
        assertEquals(seatHold.displaySeatsOnHold(), "A:2, A:3, A:4, A:5, A:6, A:7, A:8");
        assertEquals(ticketService.reserveSeats(123456, seatHold.getEmail()), "No hold found with ID of 123456");
    }

    @Test
    public void testReserveSeatsHoldSeatExpires() throws Exception {
        SeatHold seatHold = ticketService.findAndHoldSeats(3, "555@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 3);
        assertEquals(ticketService.numSeatsAvailable(), 97);
        assertEquals(seatHold.displaySeatsOnHold(), "A:4, A:5, A:6");
        Thread.sleep(2000);
        assertEquals(ticketService.reserveSeats(seatHold.getId(), seatHold.getEmail()), "The hold has expired for seats A:4, A:5, A:6");
    }

    @Test
    public void testReserveSeatsHoldBadEmail() {
        SeatHold seatHold = ticketService.findAndHoldSeats(5, "434@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 5);
        assertEquals(ticketService.numSeatsAvailable(), 95);
        assertEquals(ticketService.reserveSeats(seatHold.getId(), "fake@gmail.com"), "Customer Email either does not exist in our system or does not match up with hold Id.");
    }

    @Test
    public void testMultipleHoldsAndReserves() throws Exception {
        SeatHold seatHold = ticketService.findAndHoldSeats(5, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 5);
        assertEquals(ticketService.numSeatsAvailable(), 95);
        assertEquals(seatHold.displaySeatsOnHold(), "A:3, A:4, A:5, A:6, A:7");
        assertTrue(ticketService.reserveSeats(seatHold.getId(), seatHold.getEmail()).contains("Reservation code"));

        seatHold = ticketService.findAndHoldSeats(7, "123@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 7);
        assertEquals(ticketService.numSeatsAvailable(), 88);
        assertEquals(seatHold.displaySeatsOnHold(), "A:1, A:2, A:8, A:9, A:10, B:5, B:6");
        assertTrue(ticketService.reserveSeats(seatHold.getId(), seatHold.getEmail()).contains("Reservation code"));

        seatHold = ticketService.findAndHoldSeats(3, "555@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 3);
        assertEquals(ticketService.numSeatsAvailable(), 85);
        assertEquals(seatHold.displaySeatsOnHold(), "B:3, B:4, B:7");
        Thread.sleep(2000);

        seatHold = ticketService.findAndHoldSeats(5, "434@gmail.com");
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 5);
        assertEquals(ticketService.numSeatsAvailable(), 83);
    }

    @Test
    public void testLawnSeats() throws Exception {
        TicketService badVenue = TicketServiceFactory.create(30, 2, 1);
        assertEquals(badVenue.numSeatsAvailable(), 60);
        SeatHold seatHold = badVenue.findAndHoldSeats(60, "123@gamil.com");
        assertEquals(badVenue.numSeatsAvailable(), 0);
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 60);
        assertEquals(seatHold.displaySeatsOnHold(), "A:1, A:2, B:1, B:2, C:1, C:2, D:1, D:2, E:1, E:2, F:1," +
                " F:2, G:1, G:2, H:1, H:2, I:1, I:2, J:1, J:2, K:1, K:2, L:1, L:2, M:1, M:2, N:1, N:2, O:1, O:2, P:1, " +
                "P:2, Q:1, Q:2, R:1, R:2, S:1, S:2, T:1, T:2, U:1, U:2, V:1, V:2, W:1, W:2, X:1, X:2, Y:1, Y:2, Z:1, " +
                "Z:2, [:1, [:2, Lawn, Lawn, Lawn, Lawn, Lawn, Lawn");
        Thread.sleep(2000);
        assertEquals(badVenue.numSeatsAvailable(), 60);
        seatHold = badVenue.findAndHoldSeats(54, "123@gamil.com");
        assertTrue(badVenue.reserveSeats(seatHold.getId(), "123@gamil.com").contains("Reservation code"));
        assertEquals(badVenue.numSeatsAvailable(), 6);
        seatHold = badVenue.findAndHoldSeats(6, "abc@gamil.com");
        assertEquals(badVenue.numSeatsAvailable(), 0);
        assertEquals(seatHold.displaySeatsOnHold(), "Lawn, Lawn, Lawn, Lawn, Lawn, Lawn");
        assertTrue(badVenue.reserveSeats(seatHold.getId(), "abc@gamil.com").contains("Reservation code"));
    }
}