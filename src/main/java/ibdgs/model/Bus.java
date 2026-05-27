package ibdgs.model;

/**
 * Represents a bus in the Intelligent Bus Driver Guidance System.
 *
 * Enforces the following conditions:
 *   B1 - busID must be exactly 8 numeric digits, and must be unique (uniqueness checked by repository)
 *   B2 - capacity can only decrease or stay the same during updates
 *   B3 - drivers older than 50 cannot operate buses with capacity of 50 or more
 *   B4 - only drivers with at least 5 years experience can drive electric buses
 *   B5 - only drivers with a Heavy or PublicTransport licence can drive electric or hybrid buses
 */
public class Bus {

    // Fields
    private String busID;
    private int capacity;
    private double fuelLevel;
    private String fuelType; // Diesel, Hybrid, Electricity

    // Constructor

    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        this.busID     = busID;
        this.capacity  = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType  = fuelType;
    }


    // Validation methods

    public static boolean isValidBusID(String id) {
        if (id == null || id.length() != 8) return false;
        for (char c : id.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }


    public static boolean isValidFuelType(String fuelType) {
        return fuelType != null &&
                (fuelType.equals("Diesel")      ||
                        fuelType.equals("Hybrid")      ||
                        fuelType.equals("Electricity"));
    }


    public boolean isValid() {
        return isValidBusID(this.busID) && isValidFuelType(this.fuelType);
    }

    // B2 - Capacity update

    public boolean updateCapacity(int newCapacity) {
        if (newCapacity > this.capacity) return false;
        this.capacity = newCapacity;
        return true;
    }

    // B3, B4, B5 - Driver eligibility check

    public boolean canBeOperatedBy(Driver driver) {

        // B3: drivers older than 50 cannot drive buses with capacity 50 or more
        if (driver.getAge() > 50 && this.capacity >= 50) return false;

        // B4: electric buses require at least 5 years of experience
        if ("Electricity".equals(this.fuelType) && driver.getExperienceYears() < 5) return false;

        // B5: electric and hybrid buses require Heavy or PublicTransport licence
        if ("Electricity".equals(this.fuelType) || "Hybrid".equals(this.fuelType)) {
            String license = driver.getLicenseType();
            if (!license.equals("Heavy") && !license.equals("PublicTransport")) return false;
        }

        return true;
    }

    // Getters and Setters
    public String getBusID()     { return busID; }
    public int getCapacity()     { return capacity; }
    public double getFuelLevel() { return fuelLevel; }
    public String getFuelType()  { return fuelType; }

    public void setFuelLevel(double fuelLevel) { this.fuelLevel = fuelLevel; }

}
