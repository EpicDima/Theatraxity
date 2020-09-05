package com.epicdima.lib.dal.builders;

import com.epicdima.lib.dal.exceptions.ExecutorSqlException;
import com.epicdima.lib.dal.executors.SqlExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author EpicDima
 */
public abstract class ExecutableSqlBuilder extends SqlBuilder {

    public SqlExecutor build() {
        return new DefaultSqlExecutor(createQuery());
    }


    public static class DefaultSqlExecutor implements SqlExecutor {
        private final String sqlQuery;

        public DefaultSqlExecutor(String sqlQuery) {
            this.sqlQuery = sqlQuery;
        }

        @Override
        public synchronized void execute(Connection connection) {
            try {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sqlQuery);
                }
            } catch (SQLException e) {
                throw new ExecutorSqlException(String.format("SQL Query: %s", sqlQuery), e);
            }
        }
    }
}
