package ibdgs;

import ibdgs.model.Driver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Driver class.
 * Tests validation rules D1 through D5.
 * Each condition has at least 3 test cases covering normal, invalid, and edge cases.
 *
 * Total: 17 test cases
 */
class DriverTest {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Creates a valid Driver with the given licenseType and experienceYears.
     * All other fields use fixed valid values.
     *
     * Valid driverID "23!@abcdAB" breakdown:
     *   index 0,1  -> '2','3'          (digits 2-9)        ok
     *   index 2-7  -> '!','@','a','b','c','d'  (2 special chars)  ok
     *   index 8,9  -> 'A','B'          (uppercase letters) ok
     */
    private Driver validDriver(String licenseType, int experienceYears) {
        return new Driver(
                "23!@abcdAB",
                "John Smith",
                experienceYears,
                licenseType,
                "12|Main St|Melbourne|VIC|Australia",
                "01-06-1990"
        );
    }

    // =========================================================================
    // D1 - Driver ID Validation
    // =========================================================================

    /**
     * D1 - Normal: a correctly formatted driverID is accepted.
     */
    @Test
    void testD1_validDriverID_isAccepted() {
        assertTrue(Driver.isValidDriverID("23!@abcdAB"));
    }

    /**
     * D1 - Invalid: a driverID with fewer than 10 characters is rejected.
     */
    @Test
    void testD1_tooShort_isRejected() {
        assertFalse(Driver.isValidDriverID("23!@abAB")); // only 8 chars
    }

    /**
     * D1 - Invalid: first character '1' is outside the required range 2-9.
     */
    @Test
    void testD1_firstCharOutOfRange_isRejected() {
        assertFalse(Driver.isValidDriverID("13!@abcdAB")); // '1' is not allowed
    }

    /**
     * D1 - Invalid: only one special character in positions 3-8, at least 2 required.
     * "23abcde!AB": middle chars a,b,c,d,e,! = only 1 special char
     */
    @Test
    void testD1_insufficientSpecialChars_isRejected() {
        assertFalse(Driver.isValidDriverID("23abcde!AB"));
    }

    /**
     * D1 - Edge case: last two characters are lowercase, which is not allowed.
     * "23!@abcdab": positions 9-10 are 'a','b' (lowercase)
     */
    @Test
    void testD1_lastCharsLowercase_isRejected() {
        assertFalse(Driver.isValidDriverID("23!@abcdab"));
    }

    // =========================================================================
    // D2 - Address Format Validation
    // =========================================================================

    /**
     * D2 - Normal: a correctly pipe-delimited 5-segment address is accepted.
     */
    @Test
    void testD2_validAddress_isAccepted() {
        assertTrue(Driver.isValidAddress("12|Main St|Melbourne|VIC|Australia"));
    }

    /**
     * D2 - Invalid: an address with only 4 segments is rejected.
     */
    @Test
    void testD2_missingSegment_isRejected() {
        assertFalse(Driver.isValidAddress("12|Main St|Melbourne|VIC"));
    }

    /**
     * D2 - Invalid: address using commas instead of pipe delimiters is rejected.
     */
    @Test
    void testD2_wrongDelimiter_isRejected() {
        assertFalse(Driver.isValidAddress("12,Main St,Melbourne,VIC,Australia"));
    }

    // =========================================================================
    // D3 - Birthdate Format Validation
    // =========================================================================

    /**
     * D3 - Normal: a valid date in DD-MM-YYYY format is accepted.
     */
    @Test
    void testD3_validBirthdate_isAccepted() {
        assertTrue(Driver.isValidBirthdate("15-06-1990"));
    }

    /**
     * D3 - Invalid: birthdate in YYYY-MM-DD format does not match DD-MM-YYYY.
     */
    @Test
    void testD3_wrongFormat_isRejected() {
        assertFalse(Driver.isValidBirthdate("1990-06-15"));
    }

    /**
     * D3 - Edge case: structurally matching but impossible date is rejected.
     * Day 32 and month 13 do not exist, so strict parsing rejects this.
     */
    @Test
    void testD3_impossibleDate_isRejected() {
        assertFalse(Driver.isValidBirthdate("32-13-2000"));
    }

    // =========================================================================
    // D4 - License Type Update Restriction
    // =========================================================================

    /**
     * D4 - Normal: driver with fewer than 10 years experience can change license type.
     */
    @Test
    void testD4_experienceUnder10_licenseCanChange() {
        Driver driver = validDriver("Light", 8);
        boolean result = driver.update("23!@abcdAB", "John Smith", "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990", 8);
        assertTrue(result);
        assertEquals("Heavy", driver.getLicenseType());
    }

    /**
     * D4 - Invalid: driver with more than 10 years experience cannot change license type.
     */
    @Test
    void testD4_experienceOver10_licenseChangeRejected() {
        Driver driver = validDriver("Light", 11);
        boolean result = driver.update("23!@abcdAB", "John Smith", "Heavy",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990", 11);
        assertFalse(result);
        assertEquals("Light", driver.getLicenseType()); // unchanged
    }

    /**
     * D4 - Edge case: driver with exactly 10 years CAN still change license type.
     * The restriction only applies when experience is strictly MORE than 10.
     */
    @Test
    void testD4_experienceExactly10_licenseCanChange() {
        Driver driver = validDriver("Light", 10);
        boolean result = driver.update("23!@abcdAB", "John Smith", "Medium",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990", 10);
        assertTrue(result);
        assertEquals("Medium", driver.getLicenseType());
    }

    // =========================================================================
    // D5 - Immutable Fields (driverID and name)
    // =========================================================================

    /**
     * D5 - Invalid: attempting to change driverID during update is rejected.
     */
    @Test
    void testD5_changeDriverID_isRejected() {
        Driver driver = validDriver("Light", 5);
        boolean result = driver.update("99!@abcdZZ", "John Smith", "Light",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990", 5);
        assertFalse(result);
        assertEquals("23!@abcdAB", driver.getDriverID()); // unchanged
    }

    /**
     * D5 - Invalid: attempting to change name during update is rejected.
     */
    @Test
    void testD5_changeName_isRejected() {
        Driver driver = validDriver("Light", 5);
        boolean result = driver.update("23!@abcdAB", "Jane Doe", "Light",
                "12|Main St|Melbourne|VIC|Australia", "01-06-1990", 5);
        assertFalse(result);
        assertEquals("John Smith", driver.getName()); // unchanged
    }

    /**
     * D5 - Normal: updating a mutable field (address) while keeping driverID
     * and name unchanged is allowed.
     */
    @Test
    void testD5_changeAddress_isAllowed() {
        Driver driver = validDriver("Light", 5);
        boolean result = driver.update("23!@abcdAB", "John Smith", "Light",
                "99|New Rd|Sydney|NSW|Australia", "01-06-1990", 5);
        assertTrue(result);
        assertEquals("99|New Rd|Sydney|NSW|Australia", driver.getAddress());
    }

}
