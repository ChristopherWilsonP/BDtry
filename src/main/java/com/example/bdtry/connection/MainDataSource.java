package com.example.bdtry.connection;

import com.zaxxer.hikari.*;

import java.sql.*;

public class MainDataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/PRPrakBD_9");
        config.setUsername("postgres");
        config.setPassword("Christopher1610");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private MainDataSource() {
    }
}

