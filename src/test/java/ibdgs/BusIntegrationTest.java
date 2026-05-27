package ibdgs;

import ibdgs.model.Bus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BusRepository.
 *
 * These tests use real JSON files and real class implementations.
 * Each test starts with a clean empty file and cleans up afterwards.
 *
 * Tests verify:
 *   1. Valid buses are stored correctly
 *   2. Invalid buses are rejected
 *   3. Duplicate busIDs are rejected
 *   4. Updates are persisted correctly
 *   5. Record counts are updated correctly
 */
class BusIntegrationTest {

    private BusRepository repo;

    private static final String DATA_FILE = "data/buses.json";

    /**
     * Runs before every test.
     * Creates a fresh repository and deletes the JSON file so each test starts clean.
     */
    @BeforeEach
    void setUp() {
        repo = new BusRepository();
        new File(DATA_FILE).delete();
    }

    /**
     * Runs after every test.
     * Deletes the JSON file so tests do not affect each other.
     */
    @AfterEach
    void tearDown() {
        new File(DATA_FILE).delete();
    }

    // helper to quickly create a valid bus with a given ID
    private Bus validBus(String busID) {
        return new Bus(busID, 50, 80.0, "Diesel");
    }

    // -------------------------------------------------------------------------
    // Test 1: Valid bus is stored correctly
    // -------------------------------------------------------------------------

    /**
     * Adds a valid bus and retrieves it from the JSON file.
     * Verifies all fields are saved and loaded correctly.
     */
    @Test
    void testAddValidBus_isStoredCorrectly() {
        boolean added = repo.add(validBus("12345678"));

        // confirm add returned true
        assertTrue(added);

        // retrieve from the JSON file and verify all fields match
        Bus retrieved = repo.retrieve("12345678");
        assertNotNull(retrieved);
        assertEquals("12345678", retrieved.getBusID());
        assertEquals(50,         retrieved.getCapacity());
        assertEquals(80.0,       retrieved.getFuelLevel());
        assertEquals("Diesel",   retrieved.getFuelType());
    }

    // -------------------------------------------------------------------------
    // Test 2: Invalid bus is rejected
    // -------------------------------------------------------------------------

    /**
     * Attempts to add a bus with an invalid busID.
     * Verifies the add is rejected and nothing is written to the file.
     */
    @Test
    void testAddInvalidBus_isRejected() {
        // "ABC" fails B1 - not 8 digits
        Bus invalid = new Bus("ABC", 50, 80.0, "Diesel");
        boolean added = repo.add(invalid);

        // confirm rejected
        assertFalse(added);

        // confirm nothing was written to the file
        assertEquals(0, repo.count());
    }

    // -------------------------------------------------------------------------
    // Test 3: Duplicate busID is rejected
    // -------------------------------------------------------------------------

    /**
     * Adds a bus then tries to add another with the same busID.
     * Verifies the duplicate is rejected and only 1 record exists in the file.
     */
    @Test
    void testAddDuplicateBus_isRejected() {
        repo.add(validBus("12345678"));

        // try adding a different bus with the same busID
        Bus duplicate = new Bus("12345678", 30, 50.0, "Hybrid");
        boolean added = repo.add(duplicate);

        assertFalse(added);
        assertEquals(1, repo.count()); // still only 1 record in the file
    }

    // -------------------------------------------------------------------------
    // Test 4: Update is persisted correctly
    // -------------------------------------------------------------------------

    /**
     * Adds a bus, decreases its capacity, then retrieves from the file.
     * Verifies the new capacity was actually saved to the JSON file.
     */
    @Test
    void testUpdateBus_isPersisted() {
        repo.add(validBus("12345678"));

        // decrease capacity from 50 to 40 (B2: decrease is allowed)
        boolean updated = repo.update("12345678", 40);
        assertTrue(updated);

        // retrieve fresh from file and verify the new capacity is saved
        Bus retrieved = repo.retrieve("12345678");
        assertNotNull(retrieved);
        assertEquals(40, retrieved.getCapacity());
    }

    // -------------------------------------------------------------------------
    // Test 5: Record count updates correctly
    // -------------------------------------------------------------------------

    /**
     * Adds multiple buses one at a time.
     * Verifies the count matches the number of records in the file after each add.
     */
    @Test
    void testCount_updatesCorrectly() {
        assertEquals(0, repo.count()); // starts empty

        repo.add(validBus("12345678"));
        assertEquals(1, repo.count());

        repo.add(validBus("87654321"));
        assertEquals(2, repo.count());

        repo.add(validBus("11223344"));
        assertEquals(3, repo.count());
    }

}
