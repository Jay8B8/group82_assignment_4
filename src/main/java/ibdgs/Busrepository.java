package ibdgs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ibdgs.model.Bus;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles storing and retrieving Bus objects using a JSON file (data/buses.json).
 *
 * Supports the following operations:
 *   add()      - adds a new bus if valid and ID is unique
 *   retrieve() - finds a bus by busID
 *   update()   - updates an existing bus's capacity
 *   count()    - returns the total number of stored buses
 */
public class BusRepository {

    // path to the JSON file where buses are stored
    private static final String FILE_PATH = "data/buses.json";

    // Gson instance with pretty printing so the JSON file is human-readable
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Adds a new bus to the JSON file.
     * Returns false if:
     *   - the bus fails validation (busID or fuelType invalid)
     *   - a bus with the same busID already exists (B1 uniqueness)
     * Returns true if the bus was successfully added.
     */
    public boolean add(Bus bus) {

        // reject if any field is invalid
        if (!bus.isValid()) return false;

        List<Bus> buses = readAll();

        // B1: reject if busID already exists
        for (Bus b : buses) {
            if (b.getBusID().equals(bus.getBusID())) return false;
        }

        // add and save to file
        buses.add(bus);
        writeAll(buses);
        return true;
    }

    // Retrieve
    /**
     * Finds and returns a bus by its busID.
     * Returns null if no bus with that ID exists in the file.
     */
    public Bus retrieve(String busID) {
        for (Bus b : readAll()) {
            if (b.getBusID().equals(busID)) return b;
        }
        return null;
    }
    // Update
    /**
     * Updates an existing bus's capacity.
     * Returns false if:
     *   - no bus with the given busID is found
     *   - the new capacity is higher than the current capacity (B2)
     * Returns true if the update was successful and saved to the file.
     */
    public boolean update(String busID, int newCapacity) {

        List<Bus> buses = readAll();

        for (Bus b : buses) {
            if (b.getBusID().equals(busID)) {
                boolean success = b.updateCapacity(newCapacity);
                // only save to file if update was allowed
                if (success) writeAll(buses);
                return success;
            }
        }

        return false; // bus not found
    }

    // Count
    /**
     * Returns the total number of buses stored in the JSON file.
     */
    public int count() {
        return readAll().size();
    }

    // File helpers
    /**
     * Reads all buses from the JSON file and returns them as a list.
     * Returns an empty list if the file does not exist yet.
     */
    private List<Bus> readAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Bus>>(){}.getType();
            List<Bus> buses = gson.fromJson(reader, listType);
            return buses != null ? buses : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Writes all buses to the JSON file.
     * Creates the data/ folder automatically if it does not exist.
     */
    private void writeAll(List<Bus> buses) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(buses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
