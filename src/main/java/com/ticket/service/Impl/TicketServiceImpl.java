package com.ticket.service.Impl;

import com.ticket.service.SeatHoldFactory;
import com.ticket.service.interfaces.SeatHold;
import com.ticket.service.interfaces.TicketService;

import java.util.*;

/**
 * Ticket Service Implementation
 */
public class TicketServiceImpl implements TicketService {
    private PriorityQueue<Seat> seatsAvailable; //The queue that holds available seats
    private HashMap<Integer, SeatHold> seatHolder; //Map that holds all the seat holds objects
    private Timer timer; //Timer class that controls the expiration of holds
    private long duration; //The duration of the expiration hold in seconds
    private int cols; //Number of seats across the row

    /**
     * Ticket Service Constructor
     * @param rows - rows of the venue
     * @param cols - cols of the venue
     * @param duration - duration of the expiration hold in seconds
     * @throws RuntimeException if rows or cols is 0
     */
    public TicketServiceImpl(int rows, int cols, long duration) throws RuntimeException {
        if(rows == 0 || cols == 0) {
            throw new RuntimeException("Rows and Columns cannot be zero!");
        }
        this.cols = cols;
        this.duration = duration;
        seatHolder = new HashMap<>();
        seatsAvailable = new PriorityQueue<>(rows * cols, new Comparator<Seat>() {
            @Override
            public int compare(Seat s1, Seat s2) {
                return s1.getId() - s2.getId();
            }
        });
        createAllSeats(rows, cols);
        timer = new Timer();
    }

    @Override
    public int numSeatsAvailable() {
        return seatsAvailable.size();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        //Finds and holds the seats, 0 if fail to fill all seats requested
        HashSet<Seat> heldSeats = findSeats(numSeats);

        SeatHold seatHold = SeatHoldFactory.create(heldSeats, customerEmail, duration * 1000, cols, seatsAvailable);
        //Adds to the seatHolder map
        seatHolder.put(seatHold.getId(), seatHold);

        //Starts the hold expiration timer
        timer.schedule(seatHold.getHoldExpires(), duration * 1000);

        return seatHold;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        SeatHold seatHold = seatHolder.get(seatHoldId);

        if(seatHold == null) {
            return "No hold found with ID of " + seatHoldId;
        } else if(seatHold.hasReserved()) {
            return "Reservation code has already been given.";
        } else {
            if(seatHold.holdTimeRemaining() > 0) {
                if (customerEmail != null && seatHold.getEmail().equals(customerEmail)) {
                    seatHold.markReserved();
                    return "Reservation code is " + UUID.randomUUID().toString() + " for " + customerEmail + " for seats: " + seatHold.displaySeatsOnHold();
                } else {
                    return "Customer Email either does not exist in our system or does not match up with hold Id.";
                }
            } else {
                return "The hold has expired for seats " + seatHold.displaySeatsOnHold();
            }
        }
    }

    /**
     * Creates all the seats base on the size of the venue passed in
     *
     * Each row will reflex the following as the best seats per row:
     * A: 32 30 28 26 24 22 20 18 16 14 12 10  8  6  4  2  1 3  5  7  9  11 13 15 17 19 21 23 25 27 29 31 33
     * B: 65 63 61 59 57 55 53 51 49 47 45 43 41 39 37 35 34 36 38 40 42 44 46 48 50 52 54 56 58 60 62 64 66
     * C: ETC
     *
     * Everything After the 27 rows would be consider lawn seats
     *
     * @param rows rows of the venue
     * @param cols cols of the venue
     */
    private void createAllSeats(int rows, int cols) {
        Seat seat;
        String letter;

        int priority = 0;
        for(int row = 0; row < rows; row++) {
            letter = convertToLetter(row);
            for(int col = 0; col < cols; col++) {
                seat = new Seat(priority, letter.equals("Lawn") ? letter : letter + ":" + findSeatPerRow(col));
                seatsAvailable.add(seat);
                priority++;
            }
        }
    }

    /**
     * Converts the col index to its
     *
     * @param col - the position of the seat in the row
     * @return the seat number as the client would understand it
     */
    private int findSeatPerRow(int col) {
        int seatNumber;
        int middleSeat = (int) Math.ceil( (double) cols / 2);
        if(col == 0) {
            seatNumber = middleSeat;
        } else {
            if (col % 2 == 0) {
                seatNumber = middleSeat - (col / 2);
            } else {
                seatNumber = middleSeat + ((col + 1) / 2);
            }
        }
        return seatNumber;
    }


    /**
     * Converts number to letter for seating assignments
     *
     * @param row the row in the array that makes the venue
     * @return the letter format of the number if A-Z otherwise returns lawn for overflow
     */
    private static String convertToLetter(int row) {
        if(row < 27) {
            return String.valueOf((char)(row + 'A'));
        } else {
            return "Lawn";
        }
    }

    /**
     * Finds the best seats for the number of seats being held
     *
     * @param numSeats the number of seats
     * @return a set of the seats being held otherwise empty set
     */
    private HashSet<Seat> findSeats(int numSeats) {
        HashSet<Seat> heldSeats = new HashSet<>();
        if(numSeats > seatsAvailable.size()) {
            return heldSeats;
        } else {
            for(int count = 0; count < numSeats; count++) {
                heldSeats.add(seatsAvailable.poll());
            }
            return heldSeats;
        }
    }


    /**
     * Creates a simple way to map the seats in the venue
     *
     * Made public so test can run against it
     */
    public static class Seat {
        private int id;
        private String seatNumber;

        /**
         * Seat Constructor
         * @param id - identification of the Seat
         * @param seatNumber - seat number in a customer readable format
         */
        public Seat(int id, String seatNumber) {
            this.id = id;
            this.seatNumber = seatNumber;
        }

        /**
         * Identification of the Seat
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the seat number in a customer readable format
         * @return seat number in a customer readable format
         */
        String getSeatNumber() {
            return seatNumber;
        }
    }
}
