package rs.etf.sab.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_UserOperations implements UserOperations{
    private final Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertUser(String username, String forename, String surname, String password) {
        if(!Character.isUpperCase(forename.charAt(0)) || !Character.isUpperCase(surname.charAt(0)) || password.length() < 8){
            return false;
        }
        String sql = "INSERT INTO [User] (Username, Forename, Surname, Password, NumberSent) VALUES (?, ?, ?, ?, 0)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, forename);
            ps.setString(3, surname);
            ps.setString(4, password);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean checkUser(String username){
        String sql = "SELECT * FROM [User] WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean checkAdmin(String username){
        String sql = "SELECT * FROM Admin WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public int declareAdmin(String username) {
        if(checkAdmin(username)){
            return 1;
        }
        if(!checkUser(username)){
            return 2;
        }
        String sql = "INSERT INTO Admin (Username) VALUES (?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public Integer getSentPackages(String... usernames) {
        String sql = "SELECT NumberSent FROM [User] WHERE Username = ?";
        int packages = 0;
        for(String username : usernames){
            if(!checkUser(username)){
                return null;
            }
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    packages += rs.getInt("NumberSent");
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return packages;
    }

    @Override
    public int deleteUsers(String... usernames) {
        String sql = "DELETE FROM [User] WHERE Username = ?";
        int rows = 0;
        for(String username : usernames){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, username);
                rows += ps.executeUpdate();
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rows;
    }

    @Override
    public List<String> getAllUsers() {
        String sql = "SELECT Username FROM [User]";
        List<String> allUsers = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allUsers.add(rs.getString("Username"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allUsers;
    }
}
