package TurboChat.Server;


import java.sql.*;

public class BaseAuthService implements AuthService {

    public final String USER_TABLE = "users";
    public final String LOGIN = "login";
    public final String PASS = "pass";
    public final String NICK = "nick";

    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:sqlite:authorization.db";
        Class.forName("org.sqlite.JDBC");
        dbConnection = DriverManager.getConnection(connectionString);
        return dbConnection;
    }

    // Создаем базу (если ее нет) и добавляем тестовые записи.
    public BaseAuthService() throws SQLException, ClassNotFoundException {
        String createDB = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + "\n" +
                "(\n" +
                LOGIN +  " TEXT NOT NULL,\n" +
                PASS + " TEXT NOT NULL,\n" +
                NICK +  " TEXT NOT NULL,\n" +
                "UNIQUE(" + LOGIN + ", " +  PASS + ", " + NICK +")\n" +
                ");";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(createDB);
            prSt.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String insert = "INSERT OR IGNORE INTO " + USER_TABLE +
                " (" + LOGIN + ", " + PASS + ", " + NICK + ") " +
                "VALUES(?,?,?)";

        try(PreparedStatement prSt = getDbConnection().prepareStatement(insert)) {
            for (int i = 1; i <=3; i++) {
                prSt.setString(1, "login" + i);
                prSt.setString(2, "pass" + i);
                prSt.setString(3, "nick" + i);
                prSt.addBatch();
                prSt.executeBatch();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start() {
        System.out.println("Auth service has run.");
    }

    @Override
    public void stop() {
        System.out.println("Auth service has stopped.");
    }

    // Метод, для получения ника по логину и паролю
    @Override
    public String getNickByLoginPass(String login, String pass) {
        ResultSet rs = null;
        String getNick = "SELECT * FROM " + USER_TABLE + " WHERE login = ? AND pass = ?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(getNick);
            prSt.setString(1, login);
            prSt.setString(2, pass);
            rs = prSt.executeQuery();
            String nick = rs.getString("nick");
            if (rs != null) {
                return nick;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
