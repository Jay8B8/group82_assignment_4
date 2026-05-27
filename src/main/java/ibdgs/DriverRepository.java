package ibdgs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ibdgs.model.Driver;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles storing and retrieving Driver objects using a JSON file (data/drivers.json).
 *
 * Supports the following operations:
 *   add()      - adds a new driver if valid and ID is unique
 *   retrieve() - finds a driver by driverID
 *   update()   - updates an existing driver's details
 *   count()    - returns the total number of stored drivers
 */
public class DriverRepository {

    // path to the JSON file where drivers are stored
    private static final String FILE_PATH = "data/drivers.json";

    // Gson instance with pretty printing so the JSON file is human-readable
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Add
    /**
     * Adds a new driver to the JSON file.
     * Returns false if:
     *   - the driver fails validation (D1, D2, D3, or licenseType)
     *   - a driver with the same driverID already exists (D1 uniqueness)
     * Returns true if the driver was successfully added.
     */
    public boolean add(Driver driver) {

        // reject if any field is invalid
        if (!driver.isValid()) return false;

        List<Driver> drivers = readAll();

        // D1: reject if driverID already exists
        for (Driver d : drivers) {
            if (d.getDriverID().equals(driver.getDriverID())) return false;
        }

        // add and save to file
        drivers.add(driver);
        writeAll(drivers);
        return true;
    }

    // Retrieve
    /**
     * Finds and returns a driver by their driverID.
     * Returns null if no driver with that ID exists in the file.
     */
    public Driver retrieve(String driverID) {
        for (Driver d : readAll()) {
            if (d.getDriverID().equals(driverID)) return d;
        }
        return null;
    }
    // Update

    /**
     * Updates an existing driver's details.
     * Returns false if:
     *   - no driver with the given driverID is found
     *   - the update violates D4 (license change blocked for experience > 10)
     *   - the update violates D5 (driverID or name cannot change)
     * Returns true if the update was successful and saved to the file.
     */
    public boolean update(String driverID, String name, String newLicenseType,
                          String newAddress, String newBirthdate, int newExperienceYears) {

        List<Driver> drivers = readAll();

        for (Driver d : drivers) {
            if (d.getDriverID().equals(driverID)) {
                boolean success = d.update(driverID, name, newLicenseType,
                        newAddress, newBirthdate, newExperienceYears);
                // only save to file if update was allowed
                if (success) writeAll(drivers);
                return success;
            }
        }

        return false; // driver not found
    }
    // Count

    /**
     * Returns the total number of drivers stored in the JSON file.
     */
    public int count() {
        return readAll().size();
    }

    // File helpers
    /**
     * Reads all drivers from the JSON file and returns them as a list.
     * Returns an empty list if the file does not exist yet.
     */
    private List<Driver> readAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Driver>>(){}.getType();
            List<Driver> drivers = gson.fromJson(reader, listType);
            return drivers != null ? drivers : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Writes all drivers to the JSON file.
     * Creates the data/ folder automatically if it does not exist.
     */
    private void writeAll(List<Driver> drivers) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(drivers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
