import java.util.*;

class House {
    private String name;
    private String type;
    private String status;
    private int price;
    private int area;
    private double latitude;
    protected double longitude;

    private String description;

    public House(String name, String type, String status, int price, int area, double latitude, double longitude, String description) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getPrice() {
        return price;
    }

    public int getArea() {
        return area;
    }

    public double calculateDistance(double lat, double lon) {
        final int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat - this.latitude);
        double dLon = Math.toRadians(lon - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 10000.0) / 10000.0; // Distance rounded to 4 decimal places
    }
}

class HousingManager {
    private final Map<String, House> houses;

    public HousingManager() {
        this.houses = new HashMap<>();
    }

    public void addHouse(String name, String type, String status, int price, int area, double latitude, double longitude, String description) {
        if (houses.containsKey(name)) {
            System.out.println("invalid title");
        } else {
            House house = new House(name, type, status, price, area, latitude, longitude, description);
            houses.put(name, house);
            System.out.println("house added successfully");
        }
    }

    public void getHouse(String type, String status, Integer minPrice, Integer maxPrice, Integer minArea, Integer maxArea, double latitude, double longitude) {
        List<House> result = new ArrayList<>();

        for (House house : houses.values()) {
            if (house.getType().equals(type) && house.getStatus().equals(status)) {
                if ((minPrice == null || house.getPrice() >= minPrice) &&
                        (maxPrice == null || house.getPrice() <= maxPrice) &&
                        (minArea == null || house.getArea() >= minArea) &&
                        (maxArea == null || house.getArea() <= maxArea)) {
                    result.add(house);
                }
            }
        }

        result.sort(Comparator.comparingDouble(h -> h.calculateDistance(latitude, longitude)));

        if (result.isEmpty()) {
            System.out.println("no house found!");
        } else {
            Set<String> printedTitles = new HashSet<>();
            boolean hasDuplicates = false;

            for (House h : result) {
                if (printedTitles.contains(h.getName())) {
                    hasDuplicates = true;
                    break;
                }
                printedTitles.add(h.getName());
            }

            if (hasDuplicates) {
                System.out.println("invalid title");
            } else {
                result.forEach(h -> System.out.print(h.getName() + " "));
                System.out.println();
            }
        }
    }

    public void removeHouse(String name) {
        if (houses.remove(name) != null) {
            System.out.println("house removed successfully");
        } else {
            System.out.println("invalid title");
        }
    }
}

public class HousingListing {
    public static void main(String[] args) {
        HousingManager manager = new HousingManager();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            String command = scanner.nextLine().trim();

            if (command.startsWith("add_house")) {
                String name = extractValue(command, "-name");
                String type = extractValue(command, "-type");
                String status = extractValue(command, "-status");
                int price = Integer.parseInt(extractValue(command, "-price"));
                int area = Integer.parseInt(extractValue(command, "-area"));
                double latitude = Double.parseDouble(extractValue(command, "-latitude"));
                double longitude = Double.parseDouble(extractValue(command, "-longitude"));
                String description = extractValue(command, "-desc");

                manager.addHouse(name, type, status, price, area, latitude, longitude, description);

            } else if (command.startsWith("get_houses")) {
                String type = extractValue(command, "-type");
                String status = extractValue(command, "-status");
                Integer minPrice = extractOptionalInt(command, "-min_price");
                Integer maxPrice = extractOptionalInt(command, "-max_price");
                Integer minArea = extractOptionalInt(command, "-min_area");
                Integer maxArea = extractOptionalInt(command, "-max_area");
                double latitude = Double.parseDouble(extractValue(command, "-latitude"));
                double longitude = Double.parseDouble(extractValue(command, "-longitude"));

                manager.getHouse(type, status, minPrice, maxPrice, minArea, maxArea, latitude, longitude);

            } else if (command.startsWith("remove_house")) {
                String name = extractValue(command, "-name");
                manager.removeHouse(name);
            }
        }
        scanner.close();
    }

    private static String extractValue(String command, String key) {
        int start = command.indexOf(key + "=") + key.length() + 1;
        int end = command.indexOf(" ", start);
        if (end == -1) end = command.length();
        return command.substring(start, end).replaceAll("\"", "");
    }

    private static Integer extractOptionalInt(String command, String key) {
        if (!command.contains(key + "=")) return null;
        return Integer.parseInt(extractValue(command, key));
    }
}
