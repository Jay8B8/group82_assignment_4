package ibdgs.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Driver {
    //kjhaefbvjsefbkjsefjkhv
    // data members
    private String driverID;
    private String name;
    private int experienceYears;
    private String licenseType; // Light, Medium, Heavy, PublicTransport
    private String address;
    private String birthdate;


    private static final Pattern BIRTHDATE_REGEX = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
    private static final DateTimeFormatter BIRTHDATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    // creates Driver object (constructor)
    public Driver(String driverID, String name, int experienceYears,
                  String licenseType, String address, String birthdate) {

        // assigns data members
        this.driverID        = driverID;
        this.name            = name;
        this.experienceYears = experienceYears;
        this.licenseType     = licenseType;
        this.address         = address;
        this.birthdate       = birthdate;
    }


    // check if legnth is between 0 and 10
    public static boolean isValidDriverID(String id) {
        if (id == null || id.length() != 10) return false;

        // first two characters need to be digits between '2' and '9'
        if (id.charAt(0) < '2' || id.charAt(0) > '9') return false;
        if (id.charAt(1) < '2' || id.charAt(1) > '9') return false;

        // last two characters must be uppercase letters
        if (!Character.isUpperCase(id.charAt(8)) || !Character.isLetter(id.charAt(8))) return false;
        if (!Character.isUpperCase(id.charAt(9)) || !Character.isLetter(id.charAt(9))) return false;

        // characters at index 2 to 7 must contain at least 2 special characters
        int specialCount = 0;
        for (int i = 2; i <= 7; i++) {
            if (!Character.isLetterOrDigit(id.charAt(i))) specialCount++;
        }
        return specialCount >= 2;
    }


    // boolean method (checks if format is correct)
    public static boolean isValidAddress(String address) {
        if (address == null || address.isBlank()) return false;
        String[] parts = address.split("\\|", -1);
        if (parts.length != 5) return false;
        return Arrays.stream(parts).noneMatch(String::isBlank);
    }

    public static boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || !BIRTHDATE_REGEX.matcher(birthdate).matches()) return false;
        try {
            LocalDate.parse(birthdate, BIRTHDATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // check if license is once of the categories
    public static boolean isValidLicenseType(String licenseType) {
        return licenseType != null &&
                (licenseType.equals("Light")          ||
                        licenseType.equals("Medium")         ||
                        licenseType.equals("Heavy")          ||
                        licenseType.equals("PublicTransport"));
    }

    // runs through whether it is valid
    public boolean isValid() {
        return isValidDriverID(this.driverID)   &&
                isValidAddress(this.address)     &&
                isValidBirthdate(this.birthdate) &&
                isValidLicenseType(this.licenseType);
    }

    /**
     * Updates the driver's details.
     * Returns true if the update was successful, false if any rule was violated.
     *
     * driverID and name cannot be changed - if they differ, returns false.
     * if experienceYears is more than 10, licenseType cannot be changed.
     */

    public boolean update(String attemptedDriverID, String attemptedName,
                          String newLicenseType, String newAddress,
                          String newBirthdate, int newExperienceYears) {

        // driverID can't be changed
        if (!this.driverID.equals(attemptedDriverID)) return false;

        // name can't be changed
        if (!this.name.equals(attemptedName)) return false;

        // if experience is more than 10, licenseType cannot be changed
        if (!this.licenseType.equals(newLicenseType)) {
            if (this.experienceYears > 10) return false;
            if (!isValidLicenseType(newLicenseType)) return false;
            this.licenseType = newLicenseType;
        }

        // validate new address
        if (!isValidAddress(newAddress)) return false;
        this.address = newAddress;

        // validate new birthdate
        if (!isValidBirthdate(newBirthdate)) return false;
        this.birthdate       = newBirthdate;
        this.experienceYears = newExperienceYears;

        return true;
    }


    /**
     * Calculates and returns the driver's current age in years
     * Used by Bus.canBeOperatedBy() to check the B3 age restriction
     */
    public int getAge() {
        LocalDate dob = LocalDate.parse(this.birthdate, BIRTHDATE_FORMATTER);
        return Period.between(dob, LocalDate.now()).getYears();
    }


    // list of getters
    public String getDriverID()      { return driverID; }
    public String getName()          { return name; }
    public int getExperienceYears()  { return experienceYears; }
    public String getLicenseType()   { return licenseType; }
    public String getAddress()       { return address; }
    public String getBirthdate()     { return birthdate; }

}
