package com.ticket.service;

import com.ticket.service.Impl.TicketServiceImpl;
import com.ticket.service.interfaces.SeatHold;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class SeatHoldTest {
    private SeatHold seatHold;

    @Before
    public void setUp() throws Exception {
        PriorityQueue<TicketServiceImpl.Seat> queue = new PriorityQueue<>();
        HashSet<TicketServiceImpl.Seat> seats = new HashSet<>(); //B2, B3, C2
        TicketServiceImpl.Seat seat = new TicketServiceImpl.Seat(1, "B:2");
        seats.add(seat);
        seat = new TicketServiceImpl.Seat(6, "B:3");
        seats.add(seat);
        seat = new TicketServiceImpl.Seat( 8, "C:2");
        seats.add(seat);
        seatHold = SeatHoldFactory.create(seats, "123@gmail.com", 10000, 3, queue);
    }

    @Test
    public void getId() throws Exception {
        assertTrue(seatHold.getId() > 0);
    }

    @Test
    public void displaySeatsOnHold() throws Exception {
        assertEquals(seatHold.displaySeatsOnHold(), "B:2, B:3, C:2");
    }

    @Test
    public void holdTimeRemaining() throws Exception {
        long first = seatHold.holdTimeRemaining();
        assertTrue(seatHold.holdTimeRemaining() > 0);
        Thread.sleep(500);
        long second = seatHold.holdTimeRemaining();
        assertTrue(first > second);
    }

    @Test
    public void getEmail() throws Exception {
        assertEquals(seatHold.getEmail(), "123@gmail.com");
    }


    @Test
    public void markReservedAndHasReserved() throws Exception {
        assertFalse(seatHold.hasReserved());
        seatHold.markReserved();
        assertTrue(seatHold.hasReserved());
    }

    @Test
    public void getNumberOfSeatsOnHold() throws Exception {
        assertEquals(seatHold.getNumberOfSeatsOnHold(), 3);
    }
}