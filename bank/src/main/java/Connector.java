import java.sql.*;
import java.util.ArrayList;

public class Connector {
    private Connection connection = null;
    private Statement statement = null;
    private String url = "jdbc:postgresql://127.0.0.1:5432/bank"; // создаём URL, последнее слово – имя нашей базы данных
    private String name = "postgres"; // имя пользователя
    private String password = "1357"; // пароль (указывали при создании базы данных Postgresql

    public Connector() {
        try {
            Class.forName("org.postgresql.Driver"); // активизируем драйвер
            this.connection = DriverManager.getConnection(url, name, password); // активизируем соединение
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получение листа клиентов из БД
     *
     * @return ArrayList с клиентами
     */
    public ArrayList<Client> getAllClients() {
        ArrayList<Client> result = new ArrayList<>();
        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM client");
            while (rs.next()) {
                Client temp = new Client(rs.getInt("id"), rs.getString("name"), rs.getString("history"), new ArrayList<Count>());
                if (temp.getHistory().equals("null")) {
                    temp.setHistory("");
                }
                result.add(temp);
            }
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method getAllClients()");
        }
        return result;
    }

    /**
     * Заполнение клиента счетами из БД и установка максимального значения Bank.COUNT_ID
     *
     * @param client - ... с заполненными счетами
     */
    public void putAllCounts(Client client) {
        client.setCounts(new ArrayList<Count>());
        try {
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM count WHERE count_holder = " + client.getId());
            while (rs.next()) {
                client.getCounts().add(new Count(rs.getInt("id"), rs.getDouble("amount")));
                Bank.COUNT_ID = Math.max(Bank.COUNT_ID, rs.getInt("id"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method putAllCounts");
        }
    }

    /**
     * Сохранение всей информации о клиентах и счетах банка
     *
     * @param clients - список клиентов банка для сохранения
     */
    public void writeAndClose(ArrayList<Client> clients) {
        try {
            statement = connection.createStatement(); // создаём простейший запрос
            statement.executeUpdate("DELETE FROM count");
            statement.executeUpdate("DELETE FROM client");
            for (int i = 0; i < clients.size(); i++) {
                Client temp = clients.get(i);
                statement.executeUpdate(String.format("INSERT INTO client(id, name, history) VALUES (%d, '%s', '%s')", temp.getId(), temp.getName(), temp.getHistory()));
                for (int j = 0; j < temp.getCounts().size(); j++) {
                    Count tempC = temp.getCounts().get(j);
                    statement.executeUpdate(String.format("INSERT INTO count(id, count_holder, amount) VALUES (%d, %d, %s)", tempC.getId(), temp.getId(), String.valueOf(tempC.getAmount())));
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method writeAndClose.");
        }
    }

    public void add(int countId, double amount, String history, int clientId) {
        String prepared = prepareDouble(amount);
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("UPDATE count set amount = %s WHERE id = %d", prepared, countId));
            statement.executeUpdate(String.format("UPDATE client SET history='%s' WHERE id=%d", history, clientId));
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method add.");
        }
    }

    private String prepareDouble(double amount) {
        String res = String.format("%.2f", amount);
        return res.replaceAll(",", ".");
    }


    public void registryClient(String name) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO client(name, history) VALUES ('%s', '')", name));
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method registryClient.");
        }
    }

    public void deleteClient(int clientId) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM client WHERE id=%d", clientId));
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method deleteClient.");
        }
    }


    public void openCount(int clientId, int id, String history) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO count(id, count_holder, amount) VALUES (%d, %d, 0)", id, clientId));
            statement.executeUpdate(String.format("UPDATE client SET history='%s' WHERE id=%d", history, clientId));
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method openCount.");
        }
    }

    public void deleteCount(int countId, int clientId, String history) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM count WHERE id=%d", countId));
            statement.executeUpdate(String.format("UPDATE client SET history='%s' WHERE id=%d", history, clientId));
        } catch (SQLException s) {
            s.printStackTrace();
            System.out.println("... in Connector class, method deleteCount.");
        }
    }

}
