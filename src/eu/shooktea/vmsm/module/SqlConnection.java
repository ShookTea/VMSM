package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VirtualMachine;
import org.json.JSONObject;

public class SqlConnection {
    public SqlConnection(MySQL sql, VirtualMachine vm) {
        SSH ssh = SSH.getModuleByName("SSH");

        sshEnabled = Boolean.TRUE.equals(sql.getSetting(vm, "ssh_enabled"));
        JSONObject obj = (JSONObject)sql.getSetting(vm, "ssh");
        if (sshEnabled && obj != null) {
            String temp;
            if (obj.has("host")) {
                sshHost = obj.getString("host");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "host")) != null) {
                sshHost = temp;
            }
            else {
                sshHost = vm.getPageRoot().getHost();
            }

            if (obj.has("username")) {
                sshUsername = obj.getString("username");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "user")) != null) {
                sshUsername = temp;
            }
            else {
                sshUsername = "";
            }

            if (obj.has("password")) {
                sshPassword = obj.getString("password");
            }
            else if (ssh.isInstalled(vm) && (temp = ssh.getStringSetting(vm, "password")) != null) {
                sshPassword = temp;
            }
            else {
                sshPassword = "";
            }

            if (obj.has("port")) {
                sshPort = Integer.parseInt(obj.getString("port"));
            }
            else {
                sshPort = 22;
            }

            if (obj.has("local_port")) {
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
