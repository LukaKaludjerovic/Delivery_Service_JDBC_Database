package rs.etf.sab.operations;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_CourierRequestOperations implements CourierRequestOperation{
    private final Connection connection = DB.getInstance().getConnection();

    private boolean checkUsername(String username){
        String sql = "SELECT * FROM [User] WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
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
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private int getIdVehicle(String registration){
        String sql = "SELECT IdVehicle FROM Vehicle WHERE Registration = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, registration);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("IdVehicle");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    @Override
    public boolean insertCourierRequest(String username, String registration) {
        if(!checkUsername(username) || !checkRegistration(registration)){
            return false;
        }
        int idVehicle = getIdVehicle(registration);
        String sql = "INSERT INTO Request (Vehicle, Username) VALUES (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idVehicle);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String username) {
        String sql = "DELETE FROM Request WHERE Courier = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String username, String registration) {
        if(!checkUsername(username) || !checkRegistration(registration)){
            return false;
        }
        int idVehicle = getIdVehicle(registration);
        String sql = "UPDATE Request SET Vehicle = ? WHERE Courier = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idVehicle);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        String sql = "SELECT Courier FROM Request";
        List<String> allRequests = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allRequests.add(rs.getString("Courier"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allRequests;
    }
    
    private boolean checkRequestUsername(String username){
        String sql = "SELECT * FROM Request WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean grantRequest(String username) {
        if(!checkRequestUsername(username)){
            return false;
        }
        String sql = "{ call spApproveRequest (?) }";
        try(CallableStatement cs = connection.prepareCall(sql)){
            cs.setString(1, username);
            cs.execute();
            return true;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
