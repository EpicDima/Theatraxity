package com.epicdima.theatraxity.dal.mysql.builders.queries;

import com.epicdima.lib.dal.builders.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author EpicDima
 */
public final class MySqlDeleteQuerySqlBuilder extends SqlBuilder {
    private static final String DELETE_FROM = "DELETE FROM";
    private static final String WHERE = "WHERE";
    private static final String PARAMETER_MARK = "= ?";
    private static final String AND = "AND";

    private String tableName;
    private final List<String> filterableColumnNames = new ArrayList<>();

    public MySqlDeleteQuerySqlBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public MySqlDeleteQuerySqlBuilder addFilterableColumn(String columnName) {
        filterableColumnNames.add(columnName);
        return this;
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(DELETE_FROM).append(" ");
        builder.append(tableName).append(" ");
        if (!filterableColumnNames.isEmpty()) {
            builder.append(WHERE).append(" ");
            for (int i = 0; i < filterableColumnNames.size(); i++) {
                builder.append("`").append(filterableColumnNames.get(i)).append("`").append(" ");
                builder.append(PARAMETER_MARK).append(" ");
                if (i < filterableColumnNames.size() - 1) {
                    builder.append(AND).append(" ");
                }
            }
        }
        builder.append(";");
        return builder.toString();
    }

    private void verify() {
        if (tableName == null) {
            throw new IllegalStateException("Table Name must be specified");
        }
    }
}
