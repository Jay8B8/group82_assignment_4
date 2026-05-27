package ibdgs;

import ibdgs.model.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DriverRepository.
 *
 * These tests use real JSON files and real class implementations.
 * Each test starts with a clean empty file and cleans up afterwards.
 *
 * Tests verify:
 *   1. Valid drivers are stored correctly
 *   2. Invalid drivers are rejected
 *   3. Duplicate driverIDs are rejected
 *   4. Updates are persisted correctly
 *   5. Record counts are updated correctly
 */
class DriverIntegrationTest {

    private DriverRepository repo;

    private static final String DATA_FILE   = "data/drivers.json";
    private static final String VALID_ID    = "23!@abcdAB";
    private static final String VALID_NAME  = "John Smith";
    private static final String VALID_ADDR  = "12|Main St|Melbourne|VIC|Australia";
    private static final String VALID_BIRTH = "01-06-1990";

    /**
     * Runs before every test.
     * Creates a fresh repository and deletes the JSON file so each test starts clean.
     */
    @BeforeEach
    void setUp() {
        repo = new DriverRepository();
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

    // helper to quickly create a valid driver with a given ID
    private Driver validDriver(String driverID) {
        return new Driver(driverID, VALID_NAME, 5, "Heavy", VALID_ADDR, VALID_BIRTH);
    }

    // -------------------------------------------------------------------------
    // Test 1: Valid driver is stored correctly
    // -------------------------------------------------------------------------

    /**
     * Adds a valid driver and retrieves it from the JSON file.
     * Verifies all fields are saved and loaded correctly.
     */
    @Test
    void testAddValidDriver_isStoredCorrectly() {
        boolean added = repo.add(validDriver(VALID_ID));

        // confirm add returned true
        assertTrue(added);

        // retrieve from the JSON file and verify all fields match
        Driver retrieved = repo.retrieve(VALID_ID);
        assertNotNull(retrieved);
        assertEquals(VALID_ID,    retrieved.getDriverID());
        assertEquals(VALID_NAME,  retrieved.getName());
        assertEquals("Heavy",     retrieved.getLicenseType());
        assertEquals(VALID_ADDR,  retrieved.getAddress());
        assertEquals(VALID_BIRTH, retrieved.getBirthdate());
    }

    // -------------------------------------------------------------------------
    // Test 2: Invalid driver is rejected
    // -------------------------------------------------------------------------

    /**
     * Attempts to add a driver with an invalid driverID.
     * Verifies the add is rejected and nothing is written to the file.
     */
    @Test
    void testAddInvalidDriver_isRejected() {
        // "INVALID" fails D1 - not 10 chars, no digits or special chars in the right positions
        Driver invalid = new Driver("INVALID", "Jane Doe", 3,
                                    "Light", VALID_ADDR, VALID_BIRTH);
        boolean added = repo.add(invalid);

        // confirm rejected
        assertFalse(added);

        // confirm nothing was written to the file
        assertEquals(0, repo.count());
    }

    // -------------------------------------------------------------------------
    // Test 3: Duplicate driverID is rejected
    // -------------------------------------------------------------------------

    /**
     * Adds a driver then tries to add another with the same driverID.
     * Verifies the duplicate is rejected and only 1 record exists in the file.
     */
    @Test
    void testAddDuplicateDriver_isRejected() {
        repo.add(validDriver(VALID_ID));

        // try adding a different driver with the same driverID
        Driver duplicate = new Driver(VALID_ID, "Another Person", 2,
                                       "Light", VALID_ADDR, VALID_BIRTH);
        boolean added = repo.add(duplicate);

        assertFalse(added);
        assertEquals(1, repo.count()); // still only 1 record in the file
    }

    // -------------------------------------------------------------------------
    // Test 4: Update is persisted correctly
    // -------------------------------------------------------------------------

    /**
     * Adds a driver, updates their address, then retrieves from the file.
     * Verifies the new address was actually saved to the JSON file.
     */
    @Test
    void testUpdateDriver_isPersisted() {
        repo.add(validDriver(VALID_ID));

        String newAddress = "99|New Rd|Sydney|NSW|Australia";
        boolean updated = repo.update(VALID_ID, VALID_NAME, "Heavy",
                                       newAddress, VALID_BIRTH, 5);
        assertTrue(updated);

        // retrieve fresh from file and verify the new address is saved
        Driver retrieved = repo.retrieve(VALID_ID);
        assertNotNull(retrieved);
        assertEquals(newAddress, retrieved.getAddress());
    }

    // -------------------------------------------------------------------------
    // Test 5: Record count updates correctly
    // -------------------------------------------------------------------------

    /**
     * Adds multiple drivers one at a time.
     * Verifies the count matches the number of records in the file after each add.
     */
    @Test
    void testCount_updatesCorrectly() {
        assertEquals(0, repo.count()); // starts empty

        repo.add(validDriver("23!@abcdAB"));
        assertEquals(1, repo.count());

        repo.add(validDriver("34!@abcdAB"));
        assertEquals(2, repo.count());

        repo.add(validDriver("45!@abcdAB"));
        assertEquals(3, repo.count());
    }

}
