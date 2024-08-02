package rs.etf.sab.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_CityOperations implements CityOperations{
    private final Connection connection = DB.getInstance().getConnection();
    
    private boolean checkCityExists(String name, int postCode){
        String sql = "SELECT * FROM City WHERE Name = ? OR PostCode = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, name);
            ps.setInt(2, postCode);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int insertCity(String name, String postalCode) {
        String sql = "INSERT INTO City (Name, PostCode) VALUES (?, ?)";
        int postCode = Integer.parseInt(postalCode);
        if(checkCityExists(name, postCode)){
            return -1;
        }
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, name);
            ps.setInt(2, postCode);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteCity(String... names) {
        String sql = "DELETE FROM City WHERE Name = ?";
        int rows = 0;
        for(String name : names){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, name);
                rows += ps.executeUpdate();
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rows;
    }

    @Override
    public boolean deleteCity(int idCity) {
        String sql = "DELETE FROM City WHERE IdCity = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idCity);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        String sql = "SELECT IdCity FROM City";
        List<Integer> allCities = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allCities.add(rs.getInt("IdCity"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allCities;
    }
}
