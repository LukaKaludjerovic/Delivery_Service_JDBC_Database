package rs.etf.sab.operations;

import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {
    public static void main(String[] args) {
        CityOperations cityOperations = new kl210041_CityOperations();
        DistrictOperations districtOperations = new kl210041_DistrictOperations();
        CourierOperations courierOperations = new kl210041_CourierOperations();
        CourierRequestOperation courierRequestOperation = new kl210041_CourierRequestOperations();
        GeneralOperations generalOperations = new kl210041_GeneralOperations();
        UserOperations userOperations = new kl210041_UserOperations();
        VehicleOperations vehicleOperations = new kl210041_VehicleOperations();
        PackageOperations packageOperations = new kl210041_PackageOperations();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
