import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ============================================================================
 * PROJECT TITLE:
 * AI-Powered Smart Logistics Accessibility Intelligence Platform for the
 * North Eastern Region (NER)
 * ============================================================================
 * 
 * COMPLETE ALL-IN-ONE STANDALONE JAVA CONSOLE APPLICATION
 * 
 * This single file contains the Main UI loop, Enums, Models, Exception Handling,
 * and Services in one place.
 * 
 * How to run:
 *   java Main.java
 * or:
 *   javac Main.java
 *   java Main
 * ============================================================================
 */

// ============================================================================
// MAIN ENTRY POINT (UI & EVENT LOOP)
// Note: Placed first in file for Java 11+ source-code execution ('java Main.java')
// ============================================================================

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LogisticsManager manager = new LogisticsManager();

        printWelcomeBanner();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = readIntInput(scanner, "Enter your choice (1-10): ", 1, 10);

            switch (choice) {
                case 1:
                    manager.viewDistricts();
                    System.out.print("\nWould you like to register a new district? (yes/no): ");
                    if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                        handleRegisterDistrict(scanner, manager);
                    }
                    break;

                case 2:
                    manager.viewRoads();
                    System.out.print("\nWould you like to register a new road? (yes/no): ");
                    if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                        handleRegisterRoad(scanner, manager);
                    }
                    break;

                case 3:
                    handleReportIncident(scanner, manager);
                    break;

                case 4:
                    manager.viewIncidents();
                    break;

                case 5:
                    manager.viewVehicles();
                    System.out.print("\nWould you like to register a new vehicle? (yes/no): ");
                    if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                        handleRegisterVehicle(scanner, manager);
                    }
                    break;

                case 6:
                    handleUpdateVehicleStatus(scanner, manager);
                    break;

                case 7:
                    manager.viewDeliveries();
                    break;

                case 8:
                    manager.viewAlerts();
                    break;

                case 9:
                    manager.displayDashboard();
                    break;

                case 10:
                    running = false;
                    printExitBanner();
                    break;

                default:
                    System.out.println("[INVALID OPTION]: Please select a valid option from 1 to 10.");
            }

            if (running) {
                System.out.println("\nPress Enter to return to main menu...");
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    running = false;
                }
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n==================================================");
        System.out.println("       NER LOGISTICS INTELLIGENCE PLATFORM        ");
        System.out.println("==================================================");
        System.out.println(" 1. View Districts");
        System.out.println(" 2. View Roads");
        System.out.println(" 3. Report Road Incident");
        System.out.println(" 4. View Incidents");
        System.out.println(" 5. View Vehicles");
        System.out.println(" 6. Update Vehicle Status");
        System.out.println(" 7. View Deliveries");
        System.out.println(" 8. View Alerts");
        System.out.println(" 9. Logistics Dashboard");
        System.out.println("10. Exit");
        System.out.println("==================================================");
    }

    private static void handleReportIncident(Scanner scanner, LogisticsManager manager) {
        System.out.println("\n--- REPORT NEW ROAD INCIDENT ---");
        if (manager.getRoadMap().isEmpty()) {
            System.out.println("(No roads currently registered in the platform)");
        } else {
            System.out.println("Active Road IDs available: " + String.join(", ", manager.getRoadMap().keySet()));
        }
        System.out.print("Enter Road ID: ");
        String roadId = safeReadLine(scanner);

        if (roadId.isEmpty()) {
            System.out.println("[ERROR]: Road ID cannot be empty.");
            return;
        }

        if (manager.getRoad(roadId) == null) {
            System.out.println("[NOTICE]: Road with ID '" + roadId + "' is not registered yet.");
            System.out.print("Would you like to register this road now? (yes/no): ");
            if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                handleRegisterRoadWithId(scanner, manager, roadId);
            } else {
                System.out.println("Incident reporting cancelled.");
                return;
            }
        }

        System.out.println("\nSelect Incident Type:");
        System.out.println("1. LANDSLIDE     2. FLOOD         3. HEAVY_RAIN");
        System.out.println("4. ROAD_DAMAGE   5. BRIDGE_DAMAGE 6. TRAFFIC");
        int typeChoice = readIntInput(scanner, "Choose type (1-6): ", 1, 6);
        IncidentType incidentType;
        switch (typeChoice) {
            case 1: incidentType = IncidentType.LANDSLIDE; break;
            case 2: incidentType = IncidentType.FLOOD; break;
            case 3: incidentType = IncidentType.HEAVY_RAIN; break;
            case 4: incidentType = IncidentType.ROAD_DAMAGE; break;
            case 5: incidentType = IncidentType.BRIDGE_DAMAGE; break;
            case 6: incidentType = IncidentType.TRAFFIC; break;
            default: incidentType = IncidentType.ROAD_DAMAGE; break;
        }

        System.out.println("\nSelect Severity Level:");
        System.out.println("1. LOW  2. MEDIUM  3. HIGH  4. CRITICAL");
        int sevChoice = readIntInput(scanner, "Choose severity (1-4): ", 1, 4);
        Severity severity;
        switch (sevChoice) {
            case 1: severity = Severity.LOW; break;
            case 2: severity = Severity.MEDIUM; break;
            case 3: severity = Severity.HIGH; break;
            case 4: severity = Severity.CRITICAL; break;
            default: severity = Severity.MEDIUM; break;
        }

        System.out.print("Enter Landmark / Specific Location: ");
        String location = safeReadLine(scanner);
        if (location.isEmpty()) location = "NH Corridor Km Marker";

        System.out.print("Enter Incident Description: ");
        String description = safeReadLine(scanner);
        if (description.isEmpty()) description = "Hazard condition causing transport restriction.";

        System.out.print("Enter Reporter Name / Designation: ");
        String reportedBy = safeReadLine(scanner);
        if (reportedBy.isEmpty()) reportedBy = "Logistics Field Officer";

        try {
            manager.reportIncident(roadId, incidentType, severity, location, description, reportedBy);
        } catch (EntityNotFoundException e) {
            System.out.println("\n[ERROR]: " + e.getMessage());
        }
    }

    private static void handleUpdateVehicleStatus(Scanner scanner, LogisticsManager manager) {
        System.out.println("\n--- UPDATE VEHICLE STATUS ---");
        if (manager.getVehicleMap().isEmpty()) {
            System.out.println("(No vehicles currently registered in the fleet)");
        } else {
            System.out.println("Available Vehicles: " + String.join(", ", manager.getVehicleMap().keySet()));
        }
        System.out.print("Enter Vehicle ID: ");
        String vehicleId = safeReadLine(scanner);

        if (vehicleId.isEmpty()) {
            System.out.println("[ERROR]: Vehicle ID cannot be empty.");
            return;
        }

        if (manager.getVehicle(vehicleId) == null) {
            System.out.println("[NOTICE]: Vehicle with ID '" + vehicleId + "' is not registered in the fleet.");
            System.out.print("Would you like to register this vehicle now? (yes/no): ");
            if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                handleRegisterVehicleWithId(scanner, manager, vehicleId);
            } else {
                System.out.println("Vehicle status update cancelled.");
                return;
            }
        }

        System.out.println("\nSelect New Status:");
        System.out.println("1. ON_ROUTE  2. DELAYED  3. STOPPED  4. DELIVERED");
        int statusChoice = readIntInput(scanner, "Choose status (1-4): ", 1, 4);

        VehicleStatus newStatus;
        int delayHours = 0;

        switch (statusChoice) {
            case 1: newStatus = VehicleStatus.ON_ROUTE; break;
            case 2:
                newStatus = VehicleStatus.DELAYED;
                delayHours = readIntInput(scanner, "Enter estimated delay in hours (1-72): ", 1, 72);
                break;
            case 3: newStatus = VehicleStatus.STOPPED; break;
            case 4: newStatus = VehicleStatus.DELIVERED; break;
            default: newStatus = VehicleStatus.ON_ROUTE; break;
        }

        try {
            manager.updateVehicleStatus(vehicleId, newStatus, delayHours);
        } catch (EntityNotFoundException e) {
            System.out.println("\n[ERROR]: " + e.getMessage());
        }
    }

    private static void handleRegisterDistrict(Scanner scanner, LogisticsManager manager) {
        System.out.println("\n--- REGISTER NEW DISTRICT ---");
        System.out.print("Enter District ID (e.g., D101): ");
        String dId = safeReadLine(scanner);
        if (dId.isEmpty()) dId = "D" + (manager.getDistrictMap().size() + 101);

        System.out.print("Enter District Name (e.g., Guwahati, Shillong, Itanagar): ");
        String dName = safeReadLine(scanner);
        if (dName.isEmpty()) dName = "Regional Hub";

        System.out.print("Enter State (e.g., Assam, Meghalaya, Arunachal Pradesh): ");
        String state = safeReadLine(scanner);
        if (state.isEmpty()) state = "NER State";

        System.out.println("Select Connectivity Status:");
        System.out.println("1. GOOD  2. MODERATE  3. POOR  4. CRITICAL");
        int cChoice = readIntInput(scanner, "Choose status (1-4): ", 1, 4);
        ConnectivityStatus status;
        switch (cChoice) {
            case 1: status = ConnectivityStatus.GOOD; break;
            case 2: status = ConnectivityStatus.MODERATE; break;
            case 3: status = ConnectivityStatus.POOR; break;
            default: status = ConnectivityStatus.CRITICAL; break;
        }

        District d = new District(dId.toUpperCase(), dName, state, status);
        manager.addDistrict(d);
        System.out.println("[SUCCESS]: District " + d.getDistrictId() + " (" + d.getDistrictName() + ") registered!");
    }

    private static void handleRegisterRoad(Scanner scanner, LogisticsManager manager) {
        System.out.println("\n--- REGISTER NEW ROAD ---");
        System.out.print("Enter Road ID (e.g., R101): ");
        String rId = safeReadLine(scanner);
        if (rId.isEmpty()) rId = "R" + (manager.getRoadMap().size() + 101);
        handleRegisterRoadWithId(scanner, manager, rId);
    }

    private static void handleRegisterRoadWithId(Scanner scanner, LogisticsManager manager, String roadId) {
        System.out.print("Enter Road Name (e.g., Guwahati-Shillong Highway NH-6): ");
        String rName = safeReadLine(scanner);
        if (rName.isEmpty()) rName = "National Highway Corridor " + roadId;

        System.out.print("Enter Source Location (e.g., Guwahati): ");
        String src = safeReadLine(scanner);
        if (src.isEmpty()) src = "Origin Hub";

        System.out.print("Enter Destination Location (e.g., Shillong): ");
        String dst = safeReadLine(scanner);
        if (dst.isEmpty()) dst = "Destination Hub";

        System.out.print("Enter Distance in km: ");
        double dist = 100.0;
        try {
            dist = Double.parseDouble(safeReadLine(scanner));
        } catch (Exception e) {
            dist = 100.0;
        }

        System.out.println("Select Terrain Type: 1. PLAIN  2. HILLY  3. MOUNTAINOUS");
        int tChoice = readIntInput(scanner, "Choose terrain (1-3): ", 1, 3);
        TerrainType terrain = (tChoice == 1) ? TerrainType.PLAIN : (tChoice == 2 ? TerrainType.HILLY : TerrainType.MOUNTAINOUS);

        Road road = new Road(roadId.toUpperCase(), rName, src, dst, dist, RoadStatus.OPEN, terrain, RiskLevel.LOW);
        manager.addRoad(road);
        System.out.println("[SUCCESS]: Road " + road.getRoadId() + " (" + road.getSource() + " -> " + road.getDestination() + ") registered!");
    }

    private static void handleRegisterVehicle(Scanner scanner, LogisticsManager manager) {
        System.out.println("\n--- REGISTER NEW VEHICLE ---");
        System.out.print("Enter Vehicle ID (e.g., V101): ");
        String vId = safeReadLine(scanner);
        if (vId.isEmpty()) vId = "V" + (manager.getVehicleMap().size() + 101);
        handleRegisterVehicleWithId(scanner, manager, vId);
    }

    private static void handleRegisterVehicleWithId(Scanner scanner, LogisticsManager manager, String vehicleId) {
        System.out.println("Select Vehicle Type: 1. TRUCK  2. AMBULANCE  3. SUPPLY_VEHICLE");
        int vtChoice = readIntInput(scanner, "Choose type (1-3): ", 1, 3);
        VehicleType vType = (vtChoice == 1) ? VehicleType.TRUCK : (vtChoice == 2 ? VehicleType.AMBULANCE : VehicleType.SUPPLY_VEHICLE);

        System.out.println("Select Cargo: 1. MEDICINES  2. FOOD  3. CONSTRUCTION_MATERIAL  4. AGRICULTURAL_PRODUCE");
        int cChoice = readIntInput(scanner, "Choose cargo (1-4): ", 1, 4);
        CommodityType commodity;
        switch (cChoice) {
            case 1: commodity = CommodityType.MEDICINES; break;
            case 2: commodity = CommodityType.FOOD; break;
            case 3: commodity = CommodityType.CONSTRUCTION_MATERIAL; break;
            default: commodity = CommodityType.AGRICULTURAL_PRODUCE; break;
        }

        System.out.print("Enter Current Location (e.g., Guwahati): ");
        String loc = safeReadLine(scanner);
        if (loc.isEmpty()) loc = "Origin Hub";

        System.out.print("Enter Destination Location (e.g., Shillong): ");
        String dest = safeReadLine(scanner);
        if (dest.isEmpty()) dest = "Destination Hub";

        Vehicle vehicle;
        if (vType == VehicleType.AMBULANCE) {
            vehicle = new EmergencyVehicle(vehicleId.toUpperCase(), vType, commodity, loc, dest, VehicleStatus.ON_ROUTE, 0, "HIGH_PRIORITY - MEDICAL DISASTER RELIEF", true);
        } else {
            vehicle = new Vehicle(vehicleId.toUpperCase(), vType, commodity, loc, dest, VehicleStatus.ON_ROUTE, 0);
        }
        manager.addVehicle(vehicle);

        String delId = "DEL-" + vehicleId.toUpperCase();
        manager.addDelivery(new Delivery(delId, vehicle.getVehicleId(), commodity, loc, dest, DeliveryStatus.IN_TRANSIT, 0));
        System.out.println("[SUCCESS]: Vehicle " + vehicle.getVehicleId() + " (" + vehicle.getVehicleType() + ") and linked Delivery " + delId + " registered!");
    }

    public static String safeReadLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }

    private static int readIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return max;
            }
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("[INVALID]: Number must be between %d and %d. Try again.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[INVALID INPUT]: Please enter a valid integer number, not characters.");
            }
        }
    }

    private static void printWelcomeBanner() {
        System.out.println("========================================================================");
        System.out.println("     AI-POWERED SMART LOGISTICS ACCESSIBILITY INTELLIGENCE PLATFORM    ");
        System.out.println("                    FOR THE NORTH EASTERN REGION (NER)                 ");
        System.out.println("========================================================================");
        System.out.println(" Prototype Layer: Java Core + OOP + Collections + Clean Startup         ");
        System.out.println(" Designed for hilly terrain resilience, landslide alerts, and delivery  ");
        System.out.println(" monitoring across Arunachal, Assam, Manipur, Meghalaya, Mizoram,       ");
        System.out.println(" Nagaland, Sikkim, and Tripura.                                         ");
        System.out.println("========================================================================");
    }

    private static void printExitBanner() {
        System.out.println("\n========================================================================");
        System.out.println(" Thank you for using the NER Logistics Accessibility Intelligence Platform! ");
        System.out.println(" System state successfully closed. Stay safe on the highways.           ");
        System.out.println("========================================================================");
    }
}

// ============================================================================
// 1. ENUMERATIONS
// ============================================================================

enum ConnectivityStatus {
    GOOD, MODERATE, POOR, CRITICAL
}

enum RoadStatus {
    OPEN, PARTIALLY_BLOCKED, BLOCKED
}

enum TerrainType {
    PLAIN, HILLY, MOUNTAINOUS
}

enum RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum IncidentType {
    LANDSLIDE, FLOOD, HEAVY_RAIN, ROAD_DAMAGE, BRIDGE_DAMAGE, TRAFFIC
}

enum Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum VehicleType {
    TRUCK, AMBULANCE, SUPPLY_VEHICLE
}

enum CommodityType {
    MEDICINES, FOOD, CONSTRUCTION_MATERIAL, AGRICULTURAL_PRODUCE
}

enum VehicleStatus {
    ON_ROUTE, DELAYED, STOPPED, DELIVERED
}

enum DeliveryStatus {
    PENDING, IN_TRANSIT, DELAYED, DELIVERED
}

// ============================================================================
// 2. CUSTOM EXCEPTION
// ============================================================================

class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

// ============================================================================
// 3. MODEL CLASSES (ENCAPSULATION & INHERITANCE)
// ============================================================================

class District {
    private String districtId;
    private String districtName;
    private String state;
    private ConnectivityStatus connectivityStatus;

    public District(String districtId, String districtName, String state, ConnectivityStatus connectivityStatus) {
        this.districtId = districtId;
        this.districtName = districtName;
        this.state = state;
        this.connectivityStatus = connectivityStatus;
    }

    public String getDistrictId() { return districtId; }
    public String getDistrictName() { return districtName; }
    public String getState() { return state; }
    public ConnectivityStatus getConnectivityStatus() { return connectivityStatus; }

    public void setConnectivityStatus(ConnectivityStatus connectivityStatus) {
        this.connectivityStatus = connectivityStatus;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-14s | State: %-18s | Connectivity: %s",
                districtId, districtName, state, connectivityStatus);
    }
}

class Road {
    private String roadId;
    private String roadName;
    private String source;
    private String destination;
    private double distance;
    private RoadStatus accessibilityStatus;
    private TerrainType terrainType;
    private RiskLevel riskLevel;

    public Road(String roadId, String roadName, String source, String destination,
                double distance, RoadStatus accessibilityStatus, TerrainType terrainType, RiskLevel riskLevel) {
        this.roadId = roadId;
        this.roadName = roadName;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.accessibilityStatus = accessibilityStatus;
        this.terrainType = terrainType;
        this.riskLevel = riskLevel;
    }

    public String getRoadId() { return roadId; }
    public String getRoadName() { return roadName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getDistance() { return distance; }
    public RoadStatus getAccessibilityStatus() { return accessibilityStatus; }
    public TerrainType getTerrainType() { return terrainType; }
    public RiskLevel getRiskLevel() { return riskLevel; }

    public void setAccessibilityStatus(RoadStatus accessibilityStatus) { this.accessibilityStatus = accessibilityStatus; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    @Override
    public String toString() {
        return String.format("[%s] %-28s | %s -> %s (%5.1f km) | Terrain: %-11s | Status: %-17s | Risk: %s",
                roadId, roadName, source, destination, distance, terrainType, accessibilityStatus, riskLevel);
    }
}

class Incident {
    private String incidentId;
    private String roadId;
    private IncidentType incidentType;
    private String description;
    private Severity severity;
    private String location;
    private String reportedBy;
    private String reportedTime;

    public Incident(String incidentId, String roadId, IncidentType incidentType,
                    String description, Severity severity, String location, String reportedBy, String reportedTime) {
        this.incidentId = incidentId;
        this.roadId = roadId;
        this.incidentType = incidentType;
        this.description = description;
        this.severity = severity;
        this.location = location;
        this.reportedBy = reportedBy;
        this.reportedTime = reportedTime;
    }

    public String getIncidentId() { return incidentId; }
    public String getRoadId() { return roadId; }
    public IncidentType getIncidentType() { return incidentType; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public String getLocation() { return location; }
    public String getReportedBy() { return reportedBy; }
    public String getReportedTime() { return reportedTime; }

    @Override
    public String toString() {
        return String.format("[%s] Road: %-5s | Type: %-13s | Severity: %-8s | Loc: %-20s | By: %-15s | Details: %s",
                incidentId, roadId, incidentType, severity, location, reportedBy, description);
    }
}

class Vehicle {
    private String vehicleId;
    private VehicleType vehicleType;
    private CommodityType commodity;
    private String currentLocation;
    private String destination;
    private VehicleStatus status;
    private int estimatedDelay;

    public Vehicle(String vehicleId, VehicleType vehicleType, CommodityType commodity,
                   String currentLocation, String destination, VehicleStatus status, int estimatedDelay) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.commodity = commodity;
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.status = status;
        this.estimatedDelay = estimatedDelay;
    }

    public String getVehicleId() { return vehicleId; }
    public VehicleType getVehicleType() { return vehicleType; }
    public CommodityType getCommodity() { return commodity; }
    public String getCurrentLocation() { return currentLocation; }
    public String getDestination() { return destination; }
    public VehicleStatus getStatus() { return status; }
    public int getEstimatedDelay() { return estimatedDelay; }

    public void setStatus(VehicleStatus status) { this.status = status; }
    public void setEstimatedDelay(int estimatedDelay) { this.estimatedDelay = estimatedDelay; }

    public String getStatusSummary() {
        return status == VehicleStatus.DELAYED
                ? status + " (" + estimatedDelay + "h delay)"
                : status.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] Type: %-14s | Cargo: %-20s | %s -> %s | Status: %s",
                vehicleId, vehicleType, commodity, currentLocation, destination, getStatusSummary());
    }
}

// Demonstrates Inheritance (EmergencyVehicle extends Vehicle)
class EmergencyVehicle extends Vehicle {
    private String priorityLevel;
    private boolean sirenActive;

    public EmergencyVehicle(String vehicleId, VehicleType vehicleType, CommodityType commodity,
                            String currentLocation, String destination, VehicleStatus status,
                            int estimatedDelay, String priorityLevel, boolean sirenActive) {
        super(vehicleId, vehicleType, commodity, currentLocation, destination, status, estimatedDelay);
        this.priorityLevel = priorityLevel;
        this.sirenActive = sirenActive;
    }

    public String getPriorityLevel() { return priorityLevel; }
    public boolean isSirenActive() { return sirenActive; }

    @Override
    public String getStatusSummary() {
        return super.getStatusSummary() + " [EMERGENCY PRIORITY: " + priorityLevel + "]";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Siren: %s", sirenActive ? "ACTIVE" : "STANDBY");
    }
}

class Delivery {
    private String deliveryId;
    private String vehicleId;
    private CommodityType commodity;
    private String source;
    private String destination;
    private DeliveryStatus status;
    private int estimatedDelay;

    public Delivery(String deliveryId, String vehicleId, CommodityType commodity,
                    String source, String destination, DeliveryStatus status, int estimatedDelay) {
        this.deliveryId = deliveryId;
        this.vehicleId = vehicleId;
        this.commodity = commodity;
        this.source = source;
        this.destination = destination;
        this.status = status;
        this.estimatedDelay = estimatedDelay;
    }

    public String getDeliveryId() { return deliveryId; }
    public String getVehicleId() { return vehicleId; }
    public CommodityType getCommodity() { return commodity; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public DeliveryStatus getStatus() { return status; }
    public int getEstimatedDelay() { return estimatedDelay; }

    public void setStatus(DeliveryStatus status) { this.status = status; }
    public void setEstimatedDelay(int estimatedDelay) { this.estimatedDelay = estimatedDelay; }

    public String getStatusSummary() {
        return status == DeliveryStatus.DELAYED
                ? status + " (" + estimatedDelay + "h delay)"
                : status.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] Vehicle: %-5s | Cargo: %-20s | %s -> %s | Status: %s",
                deliveryId, vehicleId, commodity, source, destination, getStatusSummary());
    }
}

class Alert {
    private String alertId;
    private String title;
    private String targetEntity;
    private String reason;
    private Severity severity;
    private String timestamp;

    public Alert(String alertId, String title, String targetEntity, String reason, Severity severity, String timestamp) {
        this.alertId = alertId;
        this.title = title;
        this.targetEntity = targetEntity;
        this.reason = reason;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public String getAlertId() { return alertId; }
    public String getTitle() { return title; }
    public String getTargetEntity() { return targetEntity; }
    public String getReason() { return reason; }
    public Severity getSeverity() { return severity; }
    public String getTimestamp() { return timestamp; }

    public void displayAlertBanner() {
        System.out.println("========================================");
        System.out.println("             ! ALERT !                  ");
        System.out.println("========================================");
        System.out.println("Type     : " + title);
        System.out.println("Target   : " + targetEntity);
        System.out.println("Reason   : " + reason);
        System.out.println("Severity : " + severity);
        System.out.println("Time     : " + timestamp);
        System.out.println("========================================");
    }

    @Override
    public String toString() {
        return String.format("[%s] %-16s | Target: %-8s | Severity: %-8s | Time: %-19s | %s",
                alertId, title, targetEntity, severity, timestamp, reason);
    }
}

// ============================================================================
// 4. SERVICES
// ============================================================================

class AlertService {
    private List<Alert> alertList;
    private int alertCounter;
    private DateTimeFormatter formatter;

    public AlertService() {
        this.alertList = new ArrayList<>();
        this.alertCounter = 100;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public Alert triggerAlert(String title, String targetEntity, String reason, Severity severity) {
        alertCounter++;
        String alertId = "ALT-" + alertCounter;
        String timestamp = LocalDateTime.now().format(formatter);

        Alert alert = new Alert(alertId, title, targetEntity, reason, severity, timestamp);
        alertList.add(alert);
        alert.displayAlertBanner();
        return alert;
    }

    public void viewAllAlerts() {
        System.out.println("\n========================================================================================================");
        System.out.println("                                      SYSTEM ALERTS LOG                                                ");
        System.out.println("========================================================================================================");

        if (alertList.isEmpty()) {
            System.out.println("No alerts logged in the system. All operations normal.");
            return;
        }

        for (Alert alert : alertList) {
            System.out.println(alert);
        }
        System.out.println("========================================================================================================");
        System.out.println("Total Alerts Logged: " + alertList.size() + " | Critical Alerts: " + getCriticalAlertCount());
    }

    public int getCriticalAlertCount() {
        int count = 0;
        for (Alert a : alertList) {
            if (a.getSeverity() == Severity.CRITICAL) count++;
        }
        return count;
    }

    public List<Alert> getAlertList() { return alertList; }
}

class LogisticsManager {
    private Map<String, District> districtMap;
    private Map<String, Road> roadMap;
    private Map<String, Vehicle> vehicleMap;
    private Map<String, Delivery> deliveryMap;
    private List<Incident> incidentList;
    private AlertService alertService;
    private int incidentCounter;

    public LogisticsManager() {
        this.districtMap = new LinkedHashMap<>();
        this.roadMap = new LinkedHashMap<>();
        this.vehicleMap = new LinkedHashMap<>();
        this.deliveryMap = new LinkedHashMap<>();
        this.incidentList = new ArrayList<>();
        this.alertService = new AlertService();
        this.incidentCounter = 200;
        // Clean startup: 0 default demo data
    }

    public void addDistrict(District d) { districtMap.put(d.getDistrictId(), d); }
    public void addRoad(Road r) { roadMap.put(r.getRoadId(), r); }
    public void addVehicle(Vehicle v) { vehicleMap.put(v.getVehicleId(), v); }
    public void addDelivery(Delivery d) { deliveryMap.put(d.getDeliveryId(), d); }

    public Road getRoad(String roadId) {
        return roadId != null ? roadMap.get(roadId.toUpperCase()) : null;
    }

    public Vehicle getVehicle(String vehicleId) {
        return vehicleId != null ? vehicleMap.get(vehicleId.toUpperCase()) : null;
    }

    public District getDistrict(String districtId) {
        return districtId != null ? districtMap.get(districtId.toUpperCase()) : null;
    }

    public Map<String, District> getDistrictMap() { return districtMap; }
    public Map<String, Road> getRoadMap() { return roadMap; }
    public Map<String, Vehicle> getVehicleMap() { return vehicleMap; }
    public Map<String, Delivery> getDeliveryMap() { return deliveryMap; }

    public void viewDistricts() {
        System.out.println("\n========================================================================================");
        System.out.println("                 NORTH EASTERN REGION (NER) DISTRICT HUBS MONITOR                       ");
        System.out.println("========================================================================================");
        System.out.printf("%-8s | %-16s | %-20s | %-16s\n", "ID", "DISTRICT", "STATE", "CONNECTIVITY");
        System.out.println("----------------------------------------------------------------------------------------");
        if (districtMap.isEmpty()) {
            System.out.println("No districts currently registered in the platform.");
        } else {
            for (District d : districtMap.values()) {
                System.out.printf("%-8s | %-16s | %-20s | %-16s\n",
                        d.getDistrictId(), d.getDistrictName(), d.getState(), d.getConnectivityStatus());
            }
        }
        System.out.println("========================================================================================");
        System.out.println("Total Districts Tracked: " + districtMap.size());
    }

    public void viewRoads() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                                          NER ROAD ACCESSIBILITY MONITOR                                                ");
        System.out.println("========================================================================================================================");
        System.out.printf("%-6s | %-38s | %-11s -> %-11s | %-7s | %-12s | %-18s | %-8s\n",
                "ID", "ROAD NAME", "FROM", "TO", "DIST(KM)", "TERRAIN", "STATUS", "RISK");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        if (roadMap.isEmpty()) {
            System.out.println("No roads currently registered in the platform.");
        } else {
            for (Road r : roadMap.values()) {
                System.out.printf("%-6s | %-38s | %-11s -> %-11s | %7.1f | %-12s | %-18s | %-8s\n",
                        r.getRoadId(), r.getRoadName(), r.getSource(), r.getDestination(),
                        r.getDistance(), r.getTerrainType(), r.getAccessibilityStatus(), r.getRiskLevel());
            }
        }
        System.out.println("========================================================================================================================");
        System.out.println("Total Roads Tracked: " + roadMap.size());
    }

    public Incident reportIncident(String roadId, IncidentType type, Severity severity,
                                  String location, String description, String reportedBy)
            throws EntityNotFoundException {

        Road road = roadMap.get(roadId.toUpperCase());
        if (road == null) {
            throw new EntityNotFoundException("Road with ID '" + roadId + "' does not exist in the platform.");
        }

        incidentCounter++;
        String incidentId = "INC-" + incidentCounter;
        String reportedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Incident incident = new Incident(incidentId, road.getRoadId(), type, description, severity, location, reportedBy, reportedTime);
        incidentList.add(incident);

        System.out.println("\n[SUCCESS]: Incident " + incidentId + " successfully registered!");

        boolean statusChanged = false;
        if (type == IncidentType.LANDSLIDE || type == IncidentType.FLOOD || type == IncidentType.BRIDGE_DAMAGE) {
            if (severity == Severity.CRITICAL) {
                road.setAccessibilityStatus(RoadStatus.BLOCKED);
                road.setRiskLevel(RiskLevel.CRITICAL);
                statusChanged = true;
            } else if (severity == Severity.HIGH) {
                road.setAccessibilityStatus(RoadStatus.PARTIALLY_BLOCKED);
                road.setRiskLevel(RiskLevel.HIGH);
                statusChanged = true;
            } else if (severity == Severity.MEDIUM) {
                road.setRiskLevel(RiskLevel.MEDIUM);
            }
        } else if (type == IncidentType.ROAD_DAMAGE) {
            if (severity == Severity.CRITICAL) {
                road.setAccessibilityStatus(RoadStatus.BLOCKED);
                road.setRiskLevel(RiskLevel.CRITICAL);
                statusChanged = true;
            } else if (severity == Severity.HIGH) {
                road.setAccessibilityStatus(RoadStatus.PARTIALLY_BLOCKED);
                road.setRiskLevel(RiskLevel.HIGH);
                statusChanged = true;
            }
        } else if (type == IncidentType.HEAVY_RAIN || type == IncidentType.TRAFFIC) {
            if (severity == Severity.HIGH || severity == Severity.CRITICAL) {
                road.setRiskLevel(RiskLevel.HIGH);
            }
        }

        if (statusChanged) {
            System.out.println(">> [AUTOMATIC TRIGGER]: Road " + road.getRoadId() + " status updated to: "
                    + road.getAccessibilityStatus() + " | Risk Level: " + road.getRiskLevel());
        }

        if (road.getAccessibilityStatus() == RoadStatus.BLOCKED) {
            alertService.triggerAlert("ROAD BLOCKED", road.getRoadId() + " (" + road.getSource() + " - " + road.getDestination() + ")",
                    type + ": " + description, Severity.CRITICAL);
        } else if (road.getRiskLevel() == RiskLevel.HIGH || road.getRiskLevel() == RiskLevel.CRITICAL) {
            alertService.triggerAlert("ELEVATED ROAD RISK", road.getRoadId(),
                    type + " incident reported. Risk elevated to " + road.getRiskLevel(),
                    road.getRiskLevel() == RiskLevel.CRITICAL ? Severity.CRITICAL : Severity.HIGH);
        }

        return incident;
    }

    public void viewIncidents() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                                            LOGISTICS INCIDENT LOGS                                                     ");
        System.out.println("========================================================================================================================");
        System.out.printf("%-9s | %-6s | %-14s | %-9s | %-24s | %-20s | %-22s\n",
                "INCIDENT", "ROAD", "TYPE", "SEVERITY", "LOCATION", "REPORTED BY", "TIME");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        if (incidentList.isEmpty()) {
            System.out.println("No active incidents reported. All corridors operating smoothly.");
        } else {
            for (Incident inc : incidentList) {
                System.out.printf("%-9s | %-6s | %-14s | %-9s | %-24s | %-20s | %-22s\n",
                        inc.getIncidentId(), inc.getRoadId(), inc.getIncidentType(),
                        inc.getSeverity(), inc.getLocation(), inc.getReportedBy(), inc.getReportedTime());
                System.out.println("          Details: " + inc.getDescription());
                System.out.println("----------------------------------------------------------------------------------------------------------------");
            }
        }
        System.out.println("Total Incidents Logged: " + incidentList.size());
    }

    public void viewVehicles() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                                       ESSENTIAL GOODS VEHICLE TRACKING                                                 ");
        System.out.println("========================================================================================================================");
        System.out.printf("%-6s | %-15s | %-22s | %-12s -> %-12s | %-22s\n",
                "ID", "TYPE", "COMMODITY", "LOCATION", "DESTINATION", "STATUS");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        if (vehicleMap.isEmpty()) {
            System.out.println("No vehicles currently registered in the fleet.");
        } else {
            for (Vehicle v : vehicleMap.values()) {
                System.out.printf("%-6s | %-15s | %-22s | %-12s -> %-12s | %-22s\n",
                        v.getVehicleId(), v.getVehicleType(), v.getCommodity(),
                        v.getCurrentLocation(), v.getDestination(), v.getStatusSummary());
            }
        }
        System.out.println("========================================================================================================================");
        System.out.println("Total Fleet Size: " + vehicleMap.size());
    }

    public void updateVehicleStatus(String vehicleId, VehicleStatus newStatus, int delayHours)
            throws EntityNotFoundException {

        Vehicle vehicle = vehicleMap.get(vehicleId.toUpperCase());
        if (vehicle == null) {
            throw new EntityNotFoundException("Vehicle with ID '" + vehicleId + "' does not exist in the fleet.");
        }

        vehicle.setStatus(newStatus);
        vehicle.setEstimatedDelay(newStatus == VehicleStatus.DELAYED ? delayHours : 0);
        System.out.println("\n[SUCCESS]: Vehicle " + vehicle.getVehicleId() + " status updated to " + vehicle.getStatusSummary());

        Delivery matchedDelivery = null;
        for (Delivery d : deliveryMap.values()) {
            if (d.getVehicleId().equalsIgnoreCase(vehicle.getVehicleId())) {
                matchedDelivery = d;
                break;
            }
        }

        if (matchedDelivery != null) {
            if (newStatus == VehicleStatus.DELAYED) {
                matchedDelivery.setStatus(DeliveryStatus.DELAYED);
                matchedDelivery.setEstimatedDelay(delayHours);
            } else if (newStatus == VehicleStatus.DELIVERED) {
                matchedDelivery.setStatus(DeliveryStatus.DELIVERED);
                matchedDelivery.setEstimatedDelay(0);
            } else if (newStatus == VehicleStatus.ON_ROUTE) {
                matchedDelivery.setStatus(DeliveryStatus.IN_TRANSIT);
                matchedDelivery.setEstimatedDelay(0);
            }
            System.out.println(">> [AUTOMATIC SYNC]: Consignment " + matchedDelivery.getDeliveryId()
                    + " synchronized to status: " + matchedDelivery.getStatusSummary());
        }

        if (newStatus == VehicleStatus.DELAYED) {
            alertService.triggerAlert("VEHICLE DELAYED", vehicle.getVehicleId(),
                    "Vehicle carrying " + vehicle.getCommodity() + " delayed by " + delayHours + " hour(s) en route to "
                            + vehicle.getDestination(), Severity.HIGH);
        } else if (newStatus == VehicleStatus.STOPPED) {
            alertService.triggerAlert("VEHICLE STOPPED", vehicle.getVehicleId(),
                    "Vehicle stopped unexpectedly en route to " + vehicle.getDestination(), Severity.MEDIUM);
        }
    }

    public void viewDeliveries() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                                      ESSENTIAL COMMODITY DELIVERIES MONITOR                                            ");
        System.out.println("========================================================================================================================");
        System.out.printf("%-9s | %-8s | %-22s | %-12s -> %-12s | %-22s\n",
                "DELIVERY", "VEHICLE", "COMMODITY", "ORIGIN", "DESTINATION", "STATUS");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        if (deliveryMap.isEmpty()) {
            System.out.println("No delivery consignments currently registered.");
        } else {
            for (Delivery d : deliveryMap.values()) {
                String statusDisplay = d.getStatusSummary();
                if (d.getStatus() == DeliveryStatus.DELAYED) {
                    statusDisplay += " [DELAY ALERT]";
                }
                System.out.printf("%-9s | %-8s | %-22s | %-12s -> %-12s | %-22s\n",
                        d.getDeliveryId(), d.getVehicleId(), d.getCommodity(),
                        d.getSource(), d.getDestination(), statusDisplay);
            }
        }
        System.out.println("========================================================================================================================");
        System.out.println("Total Deliveries: " + deliveryMap.size());
    }

    public void viewAlerts() {
        alertService.viewAllAlerts();
    }

    public void displayDashboard() {
        int totalDistricts = districtMap.size();
        int openRoads = 0;
        int partiallyBlockedRoads = 0;
        int blockedRoads = 0;
        int highOrCriticalRiskRoads = 0;

        for (Road r : roadMap.values()) {
            if (r.getAccessibilityStatus() == RoadStatus.OPEN) openRoads++;
            else if (r.getAccessibilityStatus() == RoadStatus.PARTIALLY_BLOCKED) partiallyBlockedRoads++;
            else if (r.getAccessibilityStatus() == RoadStatus.BLOCKED) blockedRoads++;

            if (r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL) {
                highOrCriticalRiskRoads++;
            }
        }

        int activeVehicles = 0;
        int delayedVehicles = 0;
        for (Vehicle v : vehicleMap.values()) {
            if (v.getStatus() == VehicleStatus.ON_ROUTE || v.getStatus() == VehicleStatus.DELAYED) activeVehicles++;
            if (v.getStatus() == VehicleStatus.DELAYED) delayedVehicles++;
        }

        int activeIncidents = incidentList.size();
        int criticalAlerts = alertService.getCriticalAlertCount();

        ConnectivityStatus overallConnectivity;
        double blockedRatio = (double) blockedRoads / Math.max(1, roadMap.size());
        double highRiskRatio = (double) highOrCriticalRiskRoads / Math.max(1, roadMap.size());

        if (roadMap.isEmpty()) {
            overallConnectivity = ConnectivityStatus.GOOD;
        } else if (blockedRatio > 0.35 || highRiskRatio > 0.50) {
            overallConnectivity = ConnectivityStatus.CRITICAL;
        } else if (blockedRatio > 0.15 || highRiskRatio > 0.30) {
            overallConnectivity = ConnectivityStatus.POOR;
        } else if (partiallyBlockedRoads > 0 || highRiskRatio > 0.15) {
            overallConnectivity = ConnectivityStatus.MODERATE;
        } else {
            overallConnectivity = ConnectivityStatus.GOOD;
        }

        System.out.println("\n==================================================");
        System.out.println("             NER LOGISTICS DASHBOARD              ");
        System.out.println("==================================================");
        System.out.printf("Total Districts       : %d\n", totalDistricts);
        System.out.printf("Open Roads            : %d\n", openRoads);
        System.out.printf("Partially Blocked     : %d\n", partiallyBlockedRoads);
        System.out.printf("Blocked Roads         : %d\n", blockedRoads);
        System.out.printf("High/Critical Risk    : %d\n", highOrCriticalRiskRoads);
        System.out.printf("Active Vehicles       : %d\n", activeVehicles);
        System.out.printf("Delayed Vehicles      : %d\n", delayedVehicles);
        System.out.printf("Active Incidents      : %d\n", activeIncidents);
        System.out.printf("Critical Alerts       : %d\n", criticalAlerts);
        System.out.println("--------------------------------------------------");
        System.out.printf("Overall Connectivity  : %s\n", overallConnectivity);
        System.out.println("==================================================");
    }
}
