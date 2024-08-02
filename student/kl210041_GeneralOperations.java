package rs.etf.sab.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_GeneralOperations implements GeneralOperations{
    private final Connection connection = DB.getInstance().getConnection();

    @Override
    public void eraseAll() {
        String[] tables = {"Offer", "Package", "Request", "Courier", "Admin", "[User]", "Vehicle", "District", "City"};
        String sql = "DELETE FROM ";
        for(String table : tables){
            String query = sql + table;
            try(PreparedStatement ps = connection.prepareStatement(query)){
                ps.executeUpdate();
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
