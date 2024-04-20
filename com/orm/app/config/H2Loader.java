package com.orm.app.config;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;

public class H2Loader {

    public static void startWebserver(Connection connection) throws SQLException {
        Server.startWebServer(connection);
    }
}
