package com.ticket.service.interfaces;

import java.util.TimerTask;

public interface SeatHold {
    /**
     * The Identification of the SeatHold
     *
     * @return the Id
     */
    int getId();

    /**
     * Gets the reserved seats
     *
     * @return a list of seat ids (A1, C5, E10, etc.)
     */
    String displaySeatsOnHold();


    /**
     * Displays the remaining time before the ticket expires and the seat return to pool
     *
     * @return time in seconds till ticket expires
     */
    long holdTimeRemaining();

    /**
     * Gets the email of the user that has the hold
     *
     * @returns the email
     */
    String getEmail();

    /**
     * Gets the TimerTask which will release the holds on all the seats
     *
     * @return the TimerTask objects
     */
    TimerTask getHoldExpires();

    /**
     * Checks if the seats have already been reserved
     * @return true if have already been reserved, false otherwise
     */
    boolean hasReserved();

    /**
     * Marks the hold as reserved so the TimeTask wont remove the reserved seats
     */
    void markReserved();

    /**
     * Gets the number of seats being held
     *
     * @return the number of seats being held
     */
    int getNumberOfSeatsOnHold();
}
