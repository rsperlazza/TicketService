package com.ticket.service.Impl;

import com.ticket.service.interfaces.SeatHold;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Seat Hold Implementation
 */
public class SeatHoldImpl implements SeatHold {
    private int id; //Identification of the SeatHold
    private int cols; //Number of seats across the row
    private int numberOfSeatsOnHold; //Number of seats being held
    private HashSet<TicketServiceImpl.Seat> seatsOnHold; //Set of seats on hold
    private String customerEmail; // customer email
    private long duration; // duration of the expiration hold in seconds
    private long startTime; // Start time for the seat hold
    private PriorityQueue<TicketServiceImpl.Seat> seatsAvailable; //The queue that holds available seats
    private boolean reserved; //Determines if the seat have been reserved or not
    private TimerTask holdExpires; //The function that will release the seats once the hold times expires

    /**
     * SeatHold Constructor
     *
     * @param seatsOnHold - seats on hold
     * @param customerEmail - customer email
     * @param duration - duration of the expiration hold in seconds
     * @param seatsAvailable - The queue that holds available seats
     */
    public SeatHoldImpl(final HashSet<TicketServiceImpl.Seat> seatsOnHold, String customerEmail, long duration, int cols, PriorityQueue<TicketServiceImpl.Seat> seatsAvailable) {
        this.seatsOnHold = seatsOnHold;
        this.numberOfSeatsOnHold = seatsOnHold.size();
        this.customerEmail = customerEmail;
        this.duration = duration;
        this.seatsAvailable = seatsAvailable;
        this.startTime = new Date().getTime();
        this.reserved = false;
        this.cols = cols;
        this.id = Math.abs(UUID.randomUUID().hashCode());
        holdExpires = new TimerTask () {
            public void run() {
                holdExpires();
            }
        };
    }

    /**
     * Releases the seats that the hold was reserving
     */
    private void holdExpires() {
        if(!reserved) {
            //Loops over the seats and adds them back to the queue
            for(TicketServiceImpl.Seat seat : seatsOnHold) {
                seatsAvailable.add(seat);
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String displaySeatsOnHold() {
        StringBuilder seatsOnHoldBuilder = new StringBuilder();
        int length = seatsOnHold.size();
        int counter = 0;

        //Sorts the seats so that it is easy for a client to read and know all their seats
        List<TicketServiceImpl.Seat> sortedSeats = seatsOnHold.stream()
                .sorted(new Comparator<TicketServiceImpl.Seat>() {
                    @Override
                    public int compare(TicketServiceImpl.Seat seat1, TicketServiceImpl.Seat seat2) {
                        return convertSeatNumber(seat1.getSeatNumber()) - convertSeatNumber(seat2.getSeatNumber());
                    }

                    private int convertSeatNumber(String seatNumber) {
                        String[] split = seatNumber.split(":");
                        if(split.length > 1) {
                            int row = (int) split[0].charAt(0) * cols;
                            int col = Integer.valueOf(split[1]);
                            return row + col;
                        } else {
                            return Integer.MAX_VALUE; //Lawn Seats are highest value
                        }
                    }
                })
                .collect(Collectors.toList());

        //Strings out the seats now that they are ordered
        for(TicketServiceImpl.Seat seat : sortedSeats) {
            if(counter < length - 1) {
                seatsOnHoldBuilder.append(seat.getSeatNumber() + ", ");
            } else {
                seatsOnHoldBuilder.append(seat.getSeatNumber());
            }
            counter++;
        }

        return seatsOnHoldBuilder.toString();
    }

    @Override
    public long holdTimeRemaining() {
        long currentTime = new Date().getTime();
        return duration - (currentTime - startTime);
    }

    @Override
    public String getEmail() {
        return customerEmail;
    }

    @Override
    public TimerTask getHoldExpires() {
        return holdExpires;
    }

    @Override
    public boolean hasReserved() {
        return this.reserved;
    }

    @Override
    public void markReserved() {
        this.reserved = true;
    }

    @Override
    public int getNumberOfSeatsOnHold() {
        return this.numberOfSeatsOnHold;
    }
}