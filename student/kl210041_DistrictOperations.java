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

public class kl210041_DistrictOperations implements DistrictOperations{
    private final Connection connection = DB.getInstance().getConnection();
    
    private boolean checkCityExists(int idCity){
        String sql = "SELECT * FROM City WHERE IdCity = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idCity);
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
    public int insertDistrict(String name, int idCity, int xCoord, int yCoord) {
        if(!checkCityExists(idCity)){
            return -1;
        }
        String sql = "INSERT INTO District (Name, XCoord, YCoord, City) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, name);
            ps.setInt(2, xCoord);
            ps.setInt(3, yCoord);
            ps.setInt(4, idCity);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteDistricts(String... names) {
        String sql = "DELETE FROM District WHERE Name = ?";
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
    public boolean deleteDistrict(int idDistrict) {
        String sql = "DELETE FROM District WHERE IdDistrict = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idDistrict);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String name) {
        String sql = "DELETE FROM District WHERE City IN (SELECT IdCity FROM City WHERE Name = ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            return rows;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int idCity) {
        String sql = "SELECT IdDistrict FROM District WHERE City = ?";
        List<Integer> allDistrictsForCity = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idCity);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allDistrictsForCity.add(rs.getInt("IdDistrict"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allDistrictsForCity;
    }

    @Override
    public List<Integer> getAllDistricts() {
        String sql = "SELECT IdDistrict FROM District";
        List<Integer> allDistricts = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allDistricts.add(rs.getInt("IdDistrict"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allDistricts;
    }
}
