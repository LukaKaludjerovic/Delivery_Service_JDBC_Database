package rs.etf.sab.operations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_VehicleOperations implements VehicleOperations{
    private final Connection connection = DB.getInstance().getConnection();
    
    private boolean checkRegistration(String registration){
        String sql = "SELECT * FROM Vehicle WHERE Registration = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, registration);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean insertVehicle(String registration, int fuelType, BigDecimal consumption) {
        if(checkRegistration(registration)){
            return false;
        }
        String sql = "INSERT INTO Vehicle (Registration, FuelType, Consumption) VALUES (?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, registration);
            ps.setInt(2, fuelType);
            ps.setBigDecimal(3, consumption);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteVehicles(String... registrations) {
        String sql = "DELETE FROM Vehicle WHERE Registration = ?";
        int rows = 0;
        for(String registration : registrations){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, registration);
                rows += ps.executeUpdate();
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rows;
    }

    @Override
    public List<String> getAllVehichles() {
        String sql = "SELECT Registration FROM Vehicle";
        List<String> allVehicles = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allVehicles.add(rs.getString("Registration"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allVehicles;
    }

    @Override
    public boolean changeFuelType(String registration, int fuelType) {
        String sql = "UPDATE Vehicle SET FuelType = ? WHERE Registration = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, fuelType);
            ps.setString(2, registration);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeConsumption(String registration, BigDecimal consumption) {
        String sql = "UPDATE Vehicle SET Consumption = ? WHERE Registration = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1, consumption);
            ps.setString(2, registration);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
