package com.ticket.service;

import com.ticket.service.Impl.TicketServiceImpl;
import com.ticket.service.interfaces.TicketService;

public class TicketServiceFactory {
    public static TicketService create(int rows, int cols, long duration) throws RuntimeException {
        return new TicketServiceImpl(rows, cols, duration);
    }
}
