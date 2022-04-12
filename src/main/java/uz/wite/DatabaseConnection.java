package uz.wite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    String url = "jdbc:postgresql://localhost:5432/zeromax";
    String username = "postgres";
    String password = "123";

    public void saveLink(Link link) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "insert into links (uzb_name, rus_name, eng_name, link) " +
                "values (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, link.getUzbName());
        preparedStatement.setString(2, link.getRusName());
        preparedStatement.setString(3, link.getEngName());
        preparedStatement.setString(4, link.getLink());
        preparedStatement.execute();
    }

    public void saveAds(Ads ads) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "insert into ads (name, description, file_id) " +
                "values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, ads.getName());
        preparedStatement.setString(2, ads.getDescription());
        preparedStatement.setString(3, ads.getFileId());
        preparedStatement.execute();
    }

    public void saveUser(User user) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "insert into users (username, phone_number, screen_id, file_id, description) " +
                "values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPhoneNumber());
        preparedStatement.setString(3, user.getScreenId());
        preparedStatement.setString(4, user.getFileId());
        preparedStatement.setString(5, user.getDescription());
        preparedStatement.execute();
    }

    public void saveAdmin(String id) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "insert into admin (chat_id) " +
                "values (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, id);
        preparedStatement.execute();
    }

    public List<Link> getAllLinks() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "SELECT * FROM links\n" +
                "ORDER BY id ASC ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Link> linkList = new ArrayList<>();
        while (resultSet.next()) {
            Link link = new Link();
            link.setUzbName(resultSet.getString(2));
            link.setRusName(resultSet.getString(3));
            link.setEngName(resultSet.getString(4));
            link.setLink(resultSet.getString(5));
            linkList.add(link);
        }
        return linkList;
    }

    public List<Ads> getAllAds() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "SELECT * FROM ads\n" +
                "ORDER BY id ASC ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Ads> adsList = new ArrayList<>();
        while (resultSet.next()) {
            Ads ads = new Ads();
            ads.setName(resultSet.getString(2));
            ads.setDescription(resultSet.getString(3));
            ads.setFileId(resultSet.getString(4));
            adsList.add(ads);
        }
        return adsList;
    }

    public List<User> getAllUser() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "SELECT * FROM users\n" +
                "ORDER BY id ASC ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<User> adsList = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString(2));
            user.setPhoneNumber(resultSet.getString(3));
            user.setScreenId(resultSet.getString(4));
            user.setFileId(resultSet.getString(5));
            user.setDescription(resultSet.getString(6));
            adsList.add(user);
        }
        return adsList;
    }

    public List<String> getAllAdmins() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "SELECT * FROM admin\n" +
                "ORDER BY id ASC ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> adsList = new ArrayList<>();
        while (resultSet.next()) {
            adsList.add(resultSet.getString(2));
        }
        return adsList;
    }

    public List<List<String>> getAllLinkNames() throws SQLException {
        List<List<String>> lists = new ArrayList<>();
        List<Link> allLinks = getAllLinks();
        allLinks.forEach(link -> {
            List<String> names = new ArrayList<>();
            names.add(link.getUzbName());
            names.add(link.getRusName());
            names.add(link.getEngName());
            lists.add(names);
        });
        return lists;
    }

    public void deleteLink(String name) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "delete from links where chat_id = '" + name + "' or rus_name = '" + name + "' or eng_name = '" + name + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public void deleteAdmin(String id) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "delete from admin where chat_id = '" + id + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public void deleteAd(String name) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "delete from ads where name = '" + name + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public void deleteUser(String name) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        Ads ad = getAdByName(name, connection);
        String query = "delete from users where file_id = '" + ad.getFileId() + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public Ads getAdByName(String name, Connection connection) throws SQLException {
        String query = "SELECT * FROM ads " +
                "where name = '" + name + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        Ads ads = new Ads();
        while (resultSet.next()) {
            ads.setName(resultSet.getString(2));
            ads.setDescription(resultSet.getString(3));
            ads.setFileId(resultSet.getString(4));
        }
        return ads;
    }

    public Ads getAdByFileId(String fileId) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        String query = "SELECT * FROM ads " +
                "where file_id = '" + fileId + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        Ads ads = new Ads();
        while (resultSet.next()) {
            ads.setName(resultSet.getString(2));
            ads.setDescription(resultSet.getString(3));
            ads.setFileId(resultSet.getString(4));
        }
        return ads;
    }
}
