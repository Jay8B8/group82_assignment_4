package ibdgs;

import ibdgs.model.Bus;
import ibdgs.model.Driver;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bus class.
 * Tests validation rules B1 through B5.
 * Each condition has at least 3 test cases covering normal, invalid, and edge cases.
 *
 * Total: 17 test cases
 */
class BusTest {

    private static final DateTimeFormatter BIRTHDATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Computes a DD-MM-YYYY birthdate string so the driver's age equals
     * exactly the given number of years today.
     */
    private String birthdateForAge(int age) {
        return LocalDate.now().minusYears(age).format(BIRTHDATE_FORMAT);
    }

    /**
     * Creates a valid Driver with the given licenseType, experienceYears, and age.
     *
     * Valid driverID "23!@abcdAB":
     *   index 0,1 -> '2','3' (digits 2-9)
     *   index 2-7 -> '!','@','a','b','c','d' (2 special chars)
     *   index 8,9 -> 'A','B' (uppercase letters)
     */
    private Driver driverWith(String licenseType, int experienceYears, int age) {
        return new Driver(
                "23!@abcdAB",
                "Test Driver",
                experienceYears,
                licenseType,
                "1|Test St|Melbourne|VIC|Australia",
                birthdateForAge(age)
        );
    }

    // =========================================================================
    // B1 - Bus ID Validation
    // =========================================================================

    /**
     * B1 - Normal: exactly 8 digits is a valid busID.
     */
    @Test
    void testB1_validBusID_isAccepted() {
        assertTrue(Bus.isValidBusID("12345678"));
    }

    /**
     * B1 - Invalid: a busID containing a non-digit character is rejected.
     */
    @Test
    void testB1_containsNonDigit_isRejected() {
        assertFalse(Bus.isValidBusID("1234567A")); // 'A' is not a digit
    }

    /**
     * B1 - Invalid: a busID with only 7 characters is rejected.
     */
    @Test
    void testB1_tooShort_isRejected() {
        assertFalse(Bus.isValidBusID("1234567")); // only 7 chars
    }

    // =========================================================================
    // B2 - Capacity Update Restriction
    // =========================================================================

    /**
     * B2 - Normal: decreasing capacity is allowed.
     */
    @Test
    void testB2_decreaseCapacity_isAllowed() {
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertTrue(bus.updateCapacity(40));
        assertEquals(40, bus.getCapacity());
    }

    /**
     * B2 - Invalid: increasing capacity is rejected.
     */
    @Test
    void testB2_increaseCapacity_isRejected() {
        Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
        assertFalse(bus.updateCapacity(50));
        assertEquals(40, bus.getCapacity()); // unchanged
    }

    /**
     * B2 - Edge case: updating to the same capacity value is allowed.
     */
    @Test
    void testB2_sameCapacity_isAllowed() {
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertTrue(bus.updateCapacity(50));
        assertEquals(50, bus.getCapacity());
    }

    // =========================================================================
    // B3 - Driver Age Restriction
    // =========================================================================

    /**
     * B3 - Normal: driver aged exactly 50 CAN drive a bus with capacity 50.
     * The restriction only applies to drivers OLDER THAN 50 (strictly greater than).
     */
    @Test
    void testB3_driverAge50_capacity50_isAllowed() {
        Driver driver = driverWith("Heavy", 10, 50); // age = 50, not > 50
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    /**
     * B3 - Invalid: driver older than 50 cannot drive a bus with capacity 50 or more.
     */
    @Test
    void testB3_driverOver50_capacity50_isRejected() {
        Driver driver = driverWith("Heavy", 10, 51); // age = 51 > 50
        Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
        assertFalse(bus.canBeOperatedBy(driver));
    }

    /**
     * B3 - Edge case: driver older than 50 CAN drive a bus with capacity 49.
     * B3 only applies when capacity is 50 or more.
     */
    @Test
    void testB3_driverOver50_capacity49_isAllowed() {
        Driver driver = driverWith("Heavy", 10, 51);
        Bus bus = new Bus("12345678", 49, 80.0, "Diesel");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    // =========================================================================
    // B4 - Electric Bus Experience Restriction
    // =========================================================================

    /**
     * B4 - Normal: driver with exactly 5 years experience can drive an electric bus.
     * The minimum is 5 years (>= 5).
     */
    @Test
    void testB4_electricBus_exactly5Years_isAllowed() {
        Driver driver = driverWith("Heavy", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Electricity");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    /**
     * B4 - Invalid: driver with fewer than 5 years experience cannot drive an electric bus.
     */
    @Test
    void testB4_electricBus_under5Years_isRejected() {
        Driver driver = driverWith("Heavy", 4, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Electricity");
        assertFalse(bus.canBeOperatedBy(driver));
    }

    /**
     * B4 - Edge case: B4 does not apply to non-electric buses.
     * A driver with only 2 years experience can still drive a diesel bus.
     */
    @Test
    void testB4_dieselBus_under5Years_isAllowed() {
        Driver driver = driverWith("Heavy", 2, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Diesel");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    // =========================================================================
    // B5 - Licence Restriction for Electric and Hybrid Buses
    // =========================================================================

    /**
     * B5 - Normal: Heavy licence can operate an electric bus.
     */
    @Test
    void testB5_heavyLicense_electricBus_isAllowed() {
        Driver driver = driverWith("Heavy", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Electricity");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    /**
     * B5 - Normal: PublicTransport licence can operate a hybrid bus.
     */
    @Test
    void testB5_publicTransportLicense_hybridBus_isAllowed() {
        Driver driver = driverWith("PublicTransport", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Hybrid");
        assertTrue(bus.canBeOperatedBy(driver));
    }

    /**
     * B5 - Invalid: Light licence cannot operate an electric bus.
     */
    @Test
    void testB5_lightLicense_electricBus_isRejected() {
        Driver driver = driverWith("Light", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Electricity");
        assertFalse(bus.canBeOperatedBy(driver));
    }

    /**
     * B5 - Invalid: Medium licence cannot operate a hybrid bus.
     */
    @Test
    void testB5_mediumLicense_hybridBus_isRejected() {
        Driver driver = driverWith("Medium", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Hybrid");
        assertFalse(bus.canBeOperatedBy(driver));
    }

    /**
     * B5 - Edge case: B5 does not apply to diesel buses.
     * A driver with a Light licence can still operate a diesel bus.
     */
    @Test
    void testB5_lightLicense_dieselBus_isAllowed() {
        Driver driver = driverWith("Light", 5, 30);
        Bus bus = new Bus("12345678", 30, 80.0, "Diesel");
        assertTrue(bus.canBeOperatedBy(driver));
    }

}
