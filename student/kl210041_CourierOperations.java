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

public class kl210041_CourierOperations implements CourierOperations{
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
    
    @Override
    public boolean insertCourier(String username, String registration) {
        if(!checkUsername(username) || !checkRegistration(registration)){
            return false;
        }
        String sql = "INSERT INTO Courier (Username, NumberDelivered, Profit, Status, Vehicle) VALUES (?, 0, 0, 0, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, registration);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourier(String username) {
        String sql = "DELETE FROM Courier WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
        String sql = "SELECT Username FROM Courier WHERE Status = ?";
        List<String> allCouriers = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allCouriers.add(rs.getString("Username"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCouriers;
    }

    @Override
    public List<String> getAllCouriers() {
        String sql = "SELECT Username FROM Courier ORDER BY Profit DESC";
        List<String> allCouriers = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allCouriers.add(rs.getString("Username"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCouriers;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        String sql = "SELECT AVG(Profit) AS AvgProfit FROM Courier WHERE NumberDelivered >= ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, numberOfDeliveries);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getBigDecimal("AvgProfit");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
