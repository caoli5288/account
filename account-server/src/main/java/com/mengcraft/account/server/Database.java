package com.mengcraft.account.server;

import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.mengcraft.account.server.entity.BeanUser;

import java.util.Properties;

/**
 * Created by on 16-4-14.
 */
public class Database {

    public static void init(Properties p) {
        DataSourceConfig db = new DataSourceConfig();
        db.setDriver(p.getProperty("account.db.driver"));
        db.setUrl(p.getProperty("account.db.uri"));
        db.setUsername(p.getProperty("account.db.user"));
        db.setPassword(p.getProperty("account.db.secure"));

        ServerConfig config = new ServerConfig();
        config.setName("account");
        config.setDataSourceConfig(db);
        config.setDefaultServer(true);
        config.addClass(BeanUser.class);

        EbeanServerFactory.create(config);
    }

}
