package net.torosamy.torosamyGuild.config;
import net.torosamy.torosamyCore.config.TorosamyConfig;


public class MainConfig extends TorosamyConfig {

    public int prefixMaxLength;
    public int prefixMinLength;

    public Database database = new Database();
    public class Database extends TorosamyConfig {
        public String host;
        public String port;
        public String database;
        public String username;
        public String password;
    }

    public AutoSave autoSave = new AutoSave();
    public class AutoSave extends TorosamyConfig {
        public boolean enabled;
        public int seconds;
    }
}
