package com.ticket.service;

import com.ticket.service.interfaces.SeatHold;
import com.ticket.service.interfaces.TicketService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class App {
    public static void main( String[] args ) throws Exception {
        System.out.println("Welcome to the Ticket Service");
        System.out.println("-----------------------------");
        System.out.println("Command Options:");
        System.out.println("0: to Exit");
        System.out.println("1: to Get Total Remaining Seats Available");
        System.out.println("2: to Hold Seats");
        System.out.println("3: to Confirm Reservation");
        runCommandPrompt();
        System.exit(0);
    }

    private static void runCommandPrompt() throws Exception {
        try {
            TicketService ts = TicketServiceFactory.create(9, 33, 60);

            System.out.print("Please Enter a Command: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int command = Integer.valueOf(reader.readLine());
            while(command > 0) {
                switch(command) {
                    case 1:
                        System.out.println("Total remaining seats available are: " + ts.numSeatsAvailable());                                                                                    
                        break;
                    case 2:
                        System.out.print("Please enter the number of seats you wish to hold? ");
                        reader = new BufferedReader(new InputStreamReader(System.in));
                        try {
                            int numberOfSeats = Integer.valueOf(reader.readLine());
                            System.out.print("Enter an email to confirm the hold: ");
                            reader = new BufferedReader(new InputStreamReader(System.in));
                            String email = reader.readLine();
                            if(isValidEmail(email)) {
                                SeatHold seatHold = ts.findAndHoldSeats(numberOfSeats, email);
                                if(seatHold.getNumberOfSeatsOnHold() == numberOfSeats) {
                                    System.out.println("Your hold id is: " + seatHold.getId() + " for seats " + seatHold.displaySeatsOnHold());
                                } else {
                                    System.out.println("Could not hold that many seats. Maximum seats available are: " + ts.numSeatsAvailable());
                                }
                            } else {
                                System.out.println("Not a valid email.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid number.");
                        }
                        break;
                    case 3:
                        System.out.print("Confirm Reservation, please enter seat hold Id: ");
                        reader = new BufferedReader(new InputStreamReader(System.in));
                        try {
                            int seatHoldId = Integer.valueOf(reader.readLine());
                            System.out.print("Enter an email to confirm the hold: ");
                            reader = new BufferedReader(new InputStreamReader(System.in));
                            String email = reader.readLine();
                            if(isValidEmail(email)) {
                                System.out.println(ts.reserveSeats(seatHoldId, email));
                            } else {
                                System.out.println("Not a valid email.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid hold Id.");
                        }
                        break;
                    default:
                        System.out.println("Command not recognized.");
                }
                System.out.print("Please Enter a Command: ");
                reader = new BufferedReader(new InputStreamReader(System.in));
                command = Integer.valueOf(reader.readLine());
            }
            System.out.println("Goodbye.");
        } catch (NumberFormatException e) {
            System.out.println("Not a valid command, please try again.");
            runCommandPrompt();
        }
    }

    /**
     * Checks if it a valid email.
     *
     * @param email string being tested
     * @return true if its a valid email, false otherwise
     */
    private static boolean isValidEmail(String email) {
        if(email == null || email.isEmpty()) {
            return false;
        } else {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            return pat.matcher(email).matches();
        }
    }
}
