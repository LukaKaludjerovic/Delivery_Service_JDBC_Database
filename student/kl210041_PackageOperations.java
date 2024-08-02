package rs.etf.sab.operations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kl210041_PackageOperations implements PackageOperations{
    private final Connection connection = DB.getInstance().getConnection();
    
    private boolean checkDistrict(int district){
        String sql = "SELECT * FROM District WHERE IdDistrict = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, district);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
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
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean checkCourier(String username){
        String sql = "SELECT * FROM Courier WHERE Username = ? AND Status = 0";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean checkPackage(int idPackage){
        String sql = "SELECT * FROM Package WHERE IdPackage = ? AND Status = 0";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private BigDecimal getDistance(int idPackage){
        BigDecimal xCoordOrigin = null;
        BigDecimal yCoordOrigin = null;
        BigDecimal xCoordDestination = null;
        BigDecimal yCoordDestination = null;
        String sql = "SELECT D1.XCoord, D1.YCoord, D2.XCoord, D2.YCoord FROM District D1, District D2, Package P WHERE D1.IdDistrict = P.Origin AND D2.IdDistrict = P.Destination AND P.IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                xCoordOrigin = rs.getBigDecimal(1);
                yCoordOrigin = rs.getBigDecimal(2);
                xCoordDestination = rs.getBigDecimal(3);
                yCoordDestination = rs.getBigDecimal(4);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(xCoordOrigin == null || yCoordOrigin == null || xCoordDestination == null || yCoordDestination == null){
            return null;
        }
        return new BigDecimal(Math.sqrt((xCoordOrigin.doubleValue() - xCoordDestination.doubleValue()) * (xCoordOrigin.doubleValue() - xCoordDestination.doubleValue()) + (yCoordOrigin.doubleValue() - yCoordDestination.doubleValue()) * (yCoordOrigin.doubleValue() - yCoordDestination.doubleValue())));
    }
    
    private BigDecimal calculatePrice(int idPackage, BigDecimal percentage){
        int type = -1;
        BigDecimal weight = null;
        String sql = "SELECT Type, Weight FROM Package WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                type = rs.getInt("Type");
                weight = rs.getBigDecimal("Weight");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(weight == null || getDistance(idPackage) == null){
            return null;
        }
        int[] startPrice = {10, 25, 75};
        int[] weightFactor = {0, 1, 2};
        int[] pricePerKg = {0, 100, 300};
        return new BigDecimal((startPrice[type] + weightFactor[type] * weight.doubleValue() * pricePerKg[type]) * getDistance(idPackage).doubleValue() * (1 + percentage.doubleValue() / 100));
    }
    
    private boolean checkCourierDriving(String username){
        String sql = "SELECT * FROM Courier WHERE Username = ? AND Status = 1";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean checkVehicleOccupied(int idVehicle, String courier){
        String sql = "SELECT * FROM Courier WHERE Status = 1 AND Vehicle = ? AND Username != ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idVehicle);
            ps.setString(2, courier);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void startDrive(String courier){
        String sql = "UPDATE Courier SET Status = 1 WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void endDrive(String courier){
        String sql = "UPDATE Courier SET Status = 0 WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void increaseSentPackages(String username){
        String sql = "UPDATE [User] SET NumberSent = NumberSent + 1 WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, username);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void increaseDeliveredPackages(String courier){
        String sql = "UPDATE Courier SET NumberDelivered = NumberDelivered + 1 WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deliverPackage(int idPackage){
        String sql = "UPDATE Package SET Status = 3 WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void drivePackage(int idPackage){
        String sql = "UPDATE Package SET Status = 2 WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private BigDecimal calculateRevenue(String courier){
        String sql = "SELECT SUM(Price) FROM Package WHERE Courier = ? AND Status = 3";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getBigDecimal(1);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void updateProfit(BigDecimal profit, String courier){
        String sql = "UPDATE Courier SET Profit = Profit + ? WHERE Username = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1, profit);
            ps.setString(2, courier);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int insertPackage(int origin, int destination, String username, int type, BigDecimal weight) {
        if(!checkDistrict(origin) || !checkDistrict(destination) || !checkUsername(username)){
            return -1;
        }
        String sql = "INSERT INTO Package ([User], Origin, Destination, Type, Weight, Status, Price, Time, Courier) VALUES (?, ?, ?, ?, ?, 0, NULL, NULL, NULL)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, username);
            ps.setInt(2, origin);
            ps.setInt(3, destination);
            ps.setInt(4, type);
            ps.setBigDecimal(5, weight);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return generatedId;
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int insertTransportOffer(String courier, int idPackage, BigDecimal percentage) {
        if(!checkCourier(courier) || !checkPackage(idPackage)){
            return -1;
        }
        String sql = "INSERT INTO Offer (Package, Percentage, Courier) VALUES (?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, idPackage);
            if(percentage == null){
                ps.setBigDecimal(2, new BigDecimal(8.0 + Math.random() * 4));
            }
            else{
                ps.setBigDecimal(2, percentage);
            }
            ps.setString(3, courier);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return generatedId;
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int idOffer) {
        int idPackage = -1;
        BigDecimal percentage = null;
        String courier = null;
        String sql = "SELECT Package, Percentage, Courier FROM Offer WHERE IdOffer = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idOffer);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                idPackage = rs.getInt("Package");
                percentage = rs.getBigDecimal("Percentage");
                courier = rs.getString("Courier");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(idPackage == -1 || percentage == null || courier == null){
            return false;
        }
        sql = "UPDATE Package SET Status = 1, Price = ?, Time = CURRENT_TIMESTAMP, Courier = ? WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1, calculatePrice(idPackage, percentage));
            ps.setString(2, courier);
            ps.setInt(3, idPackage);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        String sql = "SELECT IdOffer FROM Offer";
        List<Integer> allOffers = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allOffers.add(rs.getInt("IdOffer"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allOffers;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int idPackage) {
        String sql = "SELECT IdOffer, Percentage FROM Offer WHERE Package = ?";
        List<Pair<Integer, BigDecimal>> allOffersForPackage = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allOffersForPackage.add(new kl210041_PackageOperationsPair(rs.getInt("IdOffer"), rs.getBigDecimal("Percentage")));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allOffersForPackage;
    }

    @Override
    public boolean deletePackage(int idPackage) {
        String sql = "DELETE FROM Package WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeWeight(int idPackage, BigDecimal weight) {
        String sql = "UPDATE Package SET Weight = ? WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1, weight);
            ps.setInt(2, idPackage);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeType(int idPackage, int type) {
        String sql = "UPDATE Package SET Type = ? WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, type);
            ps.setInt(2, idPackage);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Integer getDeliveryStatus(int idPackage) {
        String sql = "SELECT Status FROM Package WHERE IdPackage = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("Status");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int idPackage) {
        String sql = "SELECT Price FROM Package WHERE IdPackage = ? AND Price IS NOT NULL";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getBigDecimal("Price");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Date getAcceptanceTime(int idPackage) {
        String sql = "SELECT Time FROM Package WHERE IdPackage = ? AND Time IS NOT NULL";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getDate("Time");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        String sql = "SELECT IdPackage FROM Package WHERE Type = ?";
        List<Integer> allPackages = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, type);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allPackages.add(rs.getInt("IdPackage"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getAllPackages() {
        String sql = "SELECT IdPackage FROM Package";
        List<Integer> allPackages = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allPackages.add(rs.getInt("IdPackage"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPackages;
    }

    @Override
    public List<Integer> getDrive(String courier) {
        if(!checkCourierDriving(courier)){
            return null;
        }
        String sql = "SELECT IdPackage FROM Package WHERE (Status = 1 OR Status = 2) AND Courier = ? ORDER BY Time ASC";
        List<Integer> awaitingPackages = new ArrayList();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                awaitingPackages.add(rs.getInt("IdPackage"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return awaitingPackages;
    }

    @Override
    public int driveNextPackage(String courier) {
        //koji je status kurira i koje vozilo vozi zadati kurir
        String sql = "SELECT Status, Vehicle FROM Courier WHERE Username = ?";
        int status = -1;
        int idVehicle = -1;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, courier);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                status = rs.getInt("Status");
                idVehicle = rs.getInt("Vehicle");
            }
            else{
                return -2;
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //provera da li je kurirovo vozilo trenutno zauzeto
        if(checkVehicleOccupied(idVehicle, courier)){
            return -2;
        }
        
        //ako kurir jos uvek ne vozi, postavi da vozi
        if(status == 0){
            startDrive(courier);
        }
        
        //paketi za dostavu, ako nema paketa kurir zavrsava voznju
        List<Integer> packages = getDrive(courier);
        if(packages.isEmpty()){
            endDrive(courier);
            return -1;
        }
        
        //prvi i potencijalno sledeci paket za dostavu koji treba pokupiti nakon isporuke prvog paketa
        int idPackage1 = packages.get(0);
        int idPackage2 = -1;
        if(packages.size() > 1){
            idPackage2 = packages.get(1);
        }
        
        //koji korisnik je poslao paket prvi paket i status isporuke tog paketa
        sql = "SELECT [User], Status FROM Package WHERE IdPackage = ?";
        String username = null;
        int deliveryStatus = -1;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idPackage1);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                username = rs.getString(1);
                deliveryStatus = rs.getInt("Status");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //povecavanje broja poslatih paketa korisnika
        if(deliveryStatus == 1){
            increaseSentPackages(username);
        }
        
        //povecavanje broja isporucenih paketa kurira i isporuka prvog paketa
        increaseDeliveredPackages(courier);
        deliverPackage(idPackage1);
        
        //postoji sledeci paket za isporuku
        if(idPackage2 != -1){
            sql = "SELECT [User] FROM Package WHERE IdPackage = ?";
            username = null;
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setInt(1, idPackage1);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    username = rs.getString(1);
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //povecavanje broja poslatih paketa korisnika, sledeci paket postaje 'pokupljen'
            increaseSentPackages(username);
            drivePackage(idPackage2);
        }
        //nema sledeceg paketa, kurir zavrsava voznju i racuna profit cele voznje
        else{
            endDrive(courier);
            
            //zarada od isporucenih paketa
            BigDecimal revenue = calculateRevenue(courier);
            
            //racunanje predjene distance u voznji prilikom isporuke paketa
            sql = "SELECT D1.XCoord, D1.YCoord, D2.XCoord, D2.YCoord FROM District D1, District D2, Package P WHERE D1.IdDistrict = P.Origin AND D2.IdDistrict = P.Destination AND P.Status = 3 AND P.Courier = ? ORDER BY P.Time ASC";
            double distance = 0.0;
            int xPrev = -1;
            int yPrev = -1;
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, courier);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    int xCoordOrigin = rs.getBigDecimal(1).intValue();
                    int yCoordOrigin = rs.getBigDecimal(2).intValue();
                    int xCoordDestination = rs.getBigDecimal(3).intValue();
                    int yCoordDestination = rs.getBigDecimal(4).intValue();
                    distance += Math.sqrt((xCoordOrigin - xCoordDestination) * (xCoordOrigin - xCoordDestination) + (yCoordOrigin - yCoordDestination) * (yCoordOrigin - yCoordDestination));
                    
                    //ako nije prvi paket kurir mora da vozi do sledeceg paketa
                    if(xPrev != -1 || yPrev != -1){
                        distance += Math.sqrt((xPrev - xCoordOrigin) * (xPrev - xCoordOrigin) + (yPrev - yCoordOrigin) * (yPrev - yCoordOrigin));
                    }
                    xPrev = xCoordDestination;
                    yPrev = yCoordDestination;
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //racunanje troskova goriva za predjenu distancu
            sql = "SELECT FuelType, Consumption FROM Vehicle V JOIN Courier C ON V.IdVehicle = C.Vehicle WHERE Username = ?";
            int fuelType = -1;
            BigDecimal consumption = null;
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, courier);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    fuelType = rs.getInt("FuelType");
                    consumption = rs.getBigDecimal("Consumption");
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(kl210041_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(consumption == null){
                return -2;
            }
            int[] fuelPrices = {15, 36, 32};
            int fuelPrice = fuelPrices[fuelType];
            BigDecimal expences = new BigDecimal(consumption.doubleValue() * fuelPrice * distance);
            
            //racunanje profita i azuriranje kurira, profit = prihod - rashod
            BigDecimal profit = new BigDecimal(revenue.doubleValue() - expences.doubleValue());
            updateProfit(profit, courier);
        }
        return idPackage1;
    }
}
