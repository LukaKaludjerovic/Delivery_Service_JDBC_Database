package rs.etf.sab.operations;

import java.math.BigDecimal;

public class Main {
    private static final CityOperations cityOperations = new kl210041_CityOperations();
    private static final DistrictOperations districtOperations = new kl210041_DistrictOperations();
    private static final CourierOperations courierOperations = new kl210041_CourierOperations();
    private static final CourierRequestOperation courierRequestOperation = new kl210041_CourierRequestOperations();
    private static final GeneralOperations generalOperations = new kl210041_GeneralOperations();
    private static final UserOperations userOperations = new kl210041_UserOperations();
    private static final VehicleOperations vehicleOperations = new kl210041_VehicleOperations();
    private static final PackageOperations packageOperations = new kl210041_PackageOperations();
    
    static double euclidean(int x1, int y1, int x2, int y2) {
        int deltaX = x1 - x2;
        int deltaY = y1 - y2;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance;
    }
    
    static BigDecimal getPackagePrice(int type, BigDecimal weight, double distance, BigDecimal percentage) {
        BigDecimal decimalPercentage = percentage.divide(new BigDecimal(100));
        switch (type) {
            case 0:
                return new BigDecimal(10.0 * distance).multiply(decimalPercentage.add(BigDecimal.ONE));
            case 1:
                return new BigDecimal(25.0 + (weight.doubleValue() * 100.0) * distance).multiply(decimalPercentage.add(BigDecimal.ONE));
            case 2:
                return new BigDecimal(75.0 + (weight.doubleValue() * 300.0) * distance).multiply(decimalPercentage.add(BigDecimal.ONE));
            default:
                return null;
        }
    }
    
    public static void main(String[] args) {
        generalOperations.eraseAll();
        
        String lastName = "Ckalja";
        String firstName = "Pero";
        String username = "perkan";
        String password = "sabi2018";
        boolean result = userOperations.insertUser(username, firstName, lastName, password);
        System.out.println(result);
        
        String licensePlate = "BG323WE";
        int fuelType = 0;
        BigDecimal fuelConsumption = new BigDecimal(8.3);
        result = vehicleOperations.insertVehicle(licensePlate, fuelType, fuelConsumption);
        System.out.println(result);
        
        result = courierRequestOperation.insertCourierRequest(username, licensePlate);
        System.out.println(result);
        result = courierRequestOperation.grantRequest(username);
        System.out.println(result);
        
        result = courierOperations.getAllCouriers().contains(username);
        System.out.println(result);

        String secondUsername = "masa";
        String secondFirstName = "Masana";
        String secondLastName = "Leposava";
        String secondPassword = "lepasampasta1";

        result = userOperations.insertUser(secondUsername, secondFirstName, secondLastName, secondPassword);
        System.out.println(result);

        String cityName = "Novo Milosevo";
        String postalCode = "21234";
        int cityId = cityOperations.insertCity(cityName, postalCode);
        System.out.println(cityId);

        int districtX = 10;
        int districtY = 2;
        int districtId = districtOperations.insertDistrict(cityName, cityId, districtX, districtY);
        System.out.println(districtId);

        int secondDistrictX = 2;
        int secondDistrictY = 10;
        int secondDistrictId = districtOperations.insertDistrict("Vojinovica", cityId, secondDistrictX, secondDistrictY);
        System.out.println(secondDistrictId);
        
        int packageType = 0;
        BigDecimal packageWeight = new BigDecimal(123);
        int packageId = packageOperations.insertPackage(districtId, secondDistrictId, username, packageType, packageWeight);
        System.out.println(packageId);
        
        double distance = euclidean(districtX, districtY, secondDistrictX, secondDistrictY);
        BigDecimal price = getPackagePrice(packageType, packageWeight, distance, new BigDecimal(5));
        int offerId = packageOperations.insertTransportOffer(username, packageId, new BigDecimal(5));
        System.out.println(offerId);
        result = packageOperations.acceptAnOffer(offerId);
        System.out.println(result);
        
        packageType = 1;
        packageWeight = new BigDecimal(321);
        int secondPackageId = packageOperations.insertPackage(secondDistrictId, districtId, username, packageType, packageWeight);
        System.out.println(secondPackageId);

        distance = euclidean(secondDistrictX, secondDistrictY, districtX, districtY);
        price = getPackagePrice(packageType, packageWeight, distance, new BigDecimal(5));
        offerId = packageOperations.insertTransportOffer(username, secondPackageId, new BigDecimal(5));
        System.out.println(offerId);
        result = packageOperations.acceptAnOffer(offerId);
        System.out.println(result);
        
        int status = packageOperations.getDeliveryStatus(packageId);
        System.out.println(status);
        
        System.out.println(packageOperations.driveNextPackage(username) == packageId);
        
        System.out.println(packageOperations.getDeliveryStatus(secondPackageId) == 3);
    }
}
