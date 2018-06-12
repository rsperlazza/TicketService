package com.ticket.service;

import com.ticket.service.Impl.SeatHoldImpl;
import com.ticket.service.Impl.TicketServiceImpl;
import com.ticket.service.interfaces.SeatHold;

import java.util.HashSet;
import java.util.PriorityQueue;

public class SeatHoldFactory {
    public static SeatHold create(HashSet<TicketServiceImpl.Seat> heldSeats, String customerEmail, long duration, int cols, PriorityQueue<TicketServiceImpl.Seat> seatsAvailable) {
        return new SeatHoldImpl(heldSeats, customerEmail, duration, cols, seatsAvailable);
    }
}
