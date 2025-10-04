package com.CarRentalSystem.database;

import com.CarRentalSystem.users.*;
import com.CarRentalSystem.users.admin.Admin;
import com.CarRentalSystem.users.client.Client;
import com.CarRentalSystem.users.member.Member;
import com.CarRentalSystem.users.receptionist.Receptionist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountDAO {
    public static Optional<Account> getAccountByLogin(String login) {
        String query = "SELECT * FROM \"Account\" WHERE \"login\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Account account = new Account();
                account.setId(UUID.fromString(resultSet.getString("id")));
                account.setLogin(resultSet.getString("login"));
                account.setPassword(resultSet.getString("password"));
                account.setUserType(resultSet.getString("user_type"));
                account.setStatus(AccountStatus.valueOf(resultSet.getString("status")));
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public static Person getPersonById(UUID id) {
        String query = "SELECT * FROM \"Person\" WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Person person = new Person();
                person.setId((UUID) resultSet.getObject("id"));
                person.setName(resultSet.getString("name"));
                person.setAddressId((UUID) resultSet.getObject("address_id"));
                person.setEmail(resultSet.getString("email"));
                person.setPhone(resultSet.getString("phone"));
                person.setGender(resultSet.getString("gender"));
                person.setBirthday(resultSet.getDate("birthday").toLocalDate());
                person.setSurname(resultSet.getString("surname"));
                return person;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Client getClientById(UUID id) {
        String query = "SELECT * FROM \"Client\" WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Client client = new Client();
                client.setId((UUID) resultSet.getObject("id"));
                client.setCreated_at(resultSet.getDate("created_at").toLocalDate());
                client.setUpdated_at(resultSet.getDate("updated_at").toLocalDate());
                return client;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Member getMemberById(UUID id) {
        String query = "SELECT * FROM \"Member\" WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setId((UUID) resultSet.getObject("id"));
                return member;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Admin getAdminById(UUID id) {
        String query = "SELECT * FROM \"Admin\" WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Admin admin = new Admin();
                admin.setId((UUID) resultSet.getObject("id"));
                admin.setPermissions(resultSet.getString("permissions"));
                return admin;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Receptionist getReceptionistById(UUID id) {
        String query = "SELECT * FROM \"Receptionist\" WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Receptionist receptionist = new Receptionist();
                receptionist.setId((UUID) resultSet.getObject("id"));
                receptionist.setDate_joined(resultSet.getDate("date_joined"));
                return receptionist;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void insertAccount(Account account) {
        String personQuery = "INSERT INTO \"Person\" (\"name\", \"address_id\", \"email\", \"phone\", \"gender\", \"birthday\", \"surname\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(personQuery)) {

            statement.setString(1, account.getName());
            statement.setObject(2, account.getAddressId());
            statement.setString(3, account.getEmail());
            statement.setString(4, account.getPhone());
            statement.setObject(5, account.getGender().name(), java.sql.Types.OTHER);
            statement.setDate(6, java.sql.Date.valueOf(account.getBirthday()));
            statement.setString(7, account.getSurname());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    account.setId(rs.getObject("id", UUID.class)); // Assuming `id` is UUID
                } else {
                    throw new RuntimeException("Insertion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String query = "INSERT INTO \"Account\" (\"id\", \"login\", \"password\", \"status\", \"user_type\") VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, account.getId());
            statement.setString(2, account.getLogin());
            statement.setString(3, account.getPassword());
            statement.setObject(4, account.getStatus().name(), java.sql.Types.OTHER);
            statement.setObject(5, account.getUserType().name(), java.sql.Types.OTHER);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertClient(Client client) {
        String userTypeQuery = "INSERT INTO \"Client\" (\"id\", \"created_at\", \"updated_at\") VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(userTypeQuery)) {

            statement.setObject(1, client.getId());
            statement.setDate(2, java.sql.Date.valueOf(client.getCreated_at()));
            statement.setDate(3, java.sql.Date.valueOf(client.getUpdated_at()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertMember(Member member) {
        String userTypeQuery = "INSERT INTO \"Member\" (\"id\") VALUES (?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(userTypeQuery)) {

            statement.setObject(1, member.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePerson(Person person) throws SQLException {
        String query = "UPDATE \"Person\" SET \"name\" = ?, \"surname\" = ?, \"birthday\" = ?, \"gender\" = ?, \"phone\" = ?, \"email\" = ? WHERE \"id\" = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, person.getName());
            statement.setString(2, person.getSurname());
            statement.setObject(3, person.getBirthday());
            statement.setObject(4, person.getGender().name(), java.sql.Types.OTHER);
            statement.setString(5, person.getPhone());
            statement.setString(6, person.getEmail());
            statement.setObject(7, person.getId());

            // Debugging
            System.out.println("Updating Person:");
            System.out.println("ID: " + person.getId());
            System.out.println("Name: " + person.getName());
            System.out.println("Surname: " + person.getSurname());
            System.out.println("Birthday: " + person.getBirthday());
            System.out.println("Gender: " + person.getGender().name());
            System.out.println("Phone: " + person.getPhone());
            System.out.println("Email: " + person.getEmail());

//            statement.executeUpdate();
        }
    }

    public static List<Client> getClients() {
        String query = """
        SELECT p.* 
        FROM "Person" p
        INNER JOIN "Client" c ON p."id" = c."id"
    """;

        List<Client> clients = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Client client = new Client();
                client.setId((UUID) resultSet.getObject("id"));
                client.setName(resultSet.getString("name"));
                client.setAddressId((UUID) resultSet.getObject("address_id"));
                client.setEmail(resultSet.getString("email"));
                client.setPhone(resultSet.getString("phone"));
                client.setGender(resultSet.getString("gender"));
                client.setBirthday(resultSet.getDate("birthday").toLocalDate());
                client.setSurname(resultSet.getString("surname"));
                clients.add(client);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching persons for clients: " + e.getMessage(), e);
        }

        return clients;
    }


}
