package net.torosamy.torosamyGuild.config;
import net.torosamy.torosamyCore.config.IConfigManage;

import java.util.List;


public class MainConfig implements IConfigManage {
    public int prefixMaxLength;
    public int prefixMinLength;
    public int deleteGuildCooldown;
    public int createTimeCondition;
    public int maxPageShow;
    public int sortRankCooldown;
    public Double resCostBlock;
    public String guiBasicInfoItem;

    public String noGuildHolder;

    public String memberDonationItem;
    public Database database = new Database();
    public class Database implements IConfigManage {
        public Boolean enabled;
        public String host;
        public String port;
        public String database;
        public String username;
        public String password;
    }

    public AutoSave autoSave = new AutoSave();
    public class AutoSave implements IConfigManage {
        public boolean enabled;
        public int minutes;
    }
    public GuildConfig defaultGuildConfig = new GuildConfig();
    public class GuildConfig implements IConfigManage {
        public Double score;
        public String color;
    }
}
