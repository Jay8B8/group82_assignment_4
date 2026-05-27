package ibdgs;

import ibdgs.model.Driver;
import ibdgs.model.Bus;

public class Main {
    public static void main(String[] args) {

        DriverRepository driverRepo = new DriverRepository();
        BusRepository busRepo = new BusRepository();

        // Add some drivers
        Driver d1 = new Driver("23!@abcdAB", "John Smith", 5, "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990");
        Driver d2 = new Driver("34!@abcdAB", "Jane Doe", 3, "Light",
                "99|High St|Sydney|NSW|Australia", "15-03-1995");

        System.out.println("Adding drivers...");
        System.out.println("Add John Smith: " + driverRepo.add(d1));
        System.out.println("Add Jane Doe: "   + driverRepo.add(d2));
        System.out.println("Driver count: "   + driverRepo.count());

        // Add some buses
        Bus b1 = new Bus("12345678", 50, 80.0, "Diesel");
        Bus b2 = new Bus("87654321", 30, 60.0, "Hybrid");

        System.out.println("\nAdding buses...");
        System.out.println("Add bus 12345678: " + busRepo.add(b1));
        System.out.println("Add bus 87654321: " + busRepo.add(b2));
        System.out.println("Bus count: "        + busRepo.count());

        // Update a driver address - this changes the JSON file
        System.out.println("\nUpdating John's address...");
        boolean updated = driverRepo.update("23!@abcdAB", "John Smith", "Heavy",
                "55|New Rd|Brisbane|QLD|Australia", "01-06-1990", 5);
        System.out.println("Update result: " + updated);

        System.out.println("\nDone. Check the data/ folder for the JSON files.");
    }
}