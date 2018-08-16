package eu.shooktea.vmsm.module.mysql;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.ssh.SSH;

import java.sql.*;

/**
 * Representation of single connection with database. Creation of instance of connection can be done either by
 * calling contructor or using {@link MySQL#createConnection()} method.
 * <p>
 * Constructing object alone only load all configuration required for opening connection. To actually use that connection
 * you must call {@link #open()} first. On the end of using that connection you must call {@link #close()} to release
 * all resources and, including registered port if SSH tunnelling is used.
 * <p>
 * If the connection is open (you can check it with {@link #isOpen()} method), you can pass single MySQL queries to
 * {@link #query(String)} method.
 */
public class SqlConnection {

    /**
     * Creates new instance of connection. That instance cannot yet accept any queries - you need to call {@link #open()}
     * first.
     * @param sql MySQL module, containing all required configuration for current virtual machine
     * @param vm current virtual machine
     */
    public SqlConnection(MySQL sql, VirtualMachine vm) {
        SSH ssh = SSH.getModuleByName("SSH");

        sshEnabled = Boolean.TRUE.equals(sql.getSetting(vm, "ssh_enabled"));
        DataModelMap obj = (DataModelMap)sql.getSetting(vm, "ssh");
        if (sshEnabled && obj != null) {
            String temp;
            if (obj.containsKey("host")) {
                sshHost = obj.getString("host");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "host")) != null) {
                sshHost = temp;
            }
            else {
                sshHost = vm.getPageRoot().getHost();
            }

            if (obj.containsKey("username")) {
                sshUsername = obj.getString("username");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "user")) != null) {
                sshUsername = temp;
            }
            else {
                sshUsername = "";
            }

            if (obj.containsKey("password")) {
                sshPassword = obj.getString("password");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "password")) != null) {
                sshPassword = temp;
            }
            else {
                sshPassword = "";
            }

            if (obj.containsKey("port")) {
                sshPort = Integer.parseInt(obj.getString("port"));
            }
            else {
                sshPort = 22;
            }

            if (obj.containsKey("local_port")) {
                localPort = Integer.parseInt(obj.getString("local_port"));
            }
            else {
                localPort = 3307;
            }
        }
        else {
            sshHost = "";
            sshUsername = "";
            sshPassword = "";
            sshPort = 0;
            localPort = 0;
        }

        String host = sql.getStringSetting(vm, "host");
        String db = sql.getStringSetting(vm, "database");
        String usr = sql.getStringSetting(vm, "username");
        String passwd = sql.getStringSetting(vm, "password");
        String port = sql.getStringSetting(vm, "port");

        sqlHost = host == null ? "127.0.0.1" : host;
        sqlDb = db == null ? "" : db;
        sqlUsername = usr == null ? "" : usr;
        sqlPassword = passwd == null ? "" : passwd;
        sqlPort = port == null ? 3306 : Integer.parseInt(port);
    }

    /**
     * Opens new connection to database. If SSH tunnelling is used, method registers port. Opening new connection
     * with SSH tunnelling is not possible if one of them is already opened, so you always need to call {@link #close()}
     * if your connection is no longer needed.
     * @throws JSchException if SSH tunnelling failed
     * @throws SQLException if connection to database failed
     * @see #close()
     */
    public void open() throws JSchException, SQLException {
        if (isOpen) return;
        int assignedPort = sqlPort;
        if (sshEnabled) assignedPort = createSshForwarding();

        String dbUrl = "jdbc:mysql://" + sqlHost + ":" + assignedPort + "/" + sqlDb;
        try {
            connection = DriverManager.getConnection(dbUrl, sqlUsername, sqlPassword);
            isOpen = true;
        } catch (SQLException ex) {
            if (session != null) session.disconnect();
            throw new SQLException(ex);
        }

    }

    /**
     * Closes connection to database. It is required to close every connection that isn't needed anymore. If connection
     * uses SSH tunnelling then opening new connection is not possible when the previous one is still not closed.
     * @throws SQLException if closing SSH tunnelling failed. It will not be thrown if connection doesn't use SSH tunnelling.
     * @see #open()
     */
    public void close() throws SQLException {
        if (!isOpen) return;
        if (session != null) session.disconnect();
        connection.close();
        isOpen = false;
    }

    /**
     * Checks if connection is currently open. Connection must be opened before sending queries to MySQL and must be
     * closed when it is no longer needed.
     * @return {@code true} if connection is open, {@code false} otherwise.
     * @see #open()
     * @see #close()
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Sends SQL query to database. If query is a {@code SELECT} query then method will return {@link ResultSet}
     * with all values returned by database, otherwise method will return {@link Integer} with count of updated rows.
     * <p>
     * Connection to database must be opened before sending queries to database. When all queries has been sent and
     * connection is no longer needed, it needs to be closed in order to release it's resources.
     * @param query SQL query
     * @return result of SQL - either {@link ResultSet} or {@link Integer}.
     * @throws SQLException if connection is not open yet.
     * @see #open()
     * @see #close()
     */
    public Object query(String query) throws SQLException {
        if (!isOpen || connection == null)
            throw new SQLException("Connection is not yet established");
        Statement statement = connection.createStatement();
        if (statement.execute(query)) {
            return statement.getResultSet();
        }
        else {
            Integer i = statement.getUpdateCount();
            statement.close();
            return i;
        }
    }

    private int createSshForwarding() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(sshUsername, sshHost, sshPort);
        session.setPassword(sshPassword);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        int assignedPort = session.setPortForwardingL(localPort, sqlHost, sqlPort);
        return assignedPort;
    }

    private boolean isOpen = false;
    private Session session;
    private Connection connection;

    private final boolean sshEnabled;
    private final String sshHost;
    private final String sshUsername;
    private final String sshPassword;
    private final int sshPort;
    private final int localPort;

    private final String sqlHost;
    private final String sqlDb;
    private final String sqlUsername;
    private final String sqlPassword;
    private final int sqlPort;
}
