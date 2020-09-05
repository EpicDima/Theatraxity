package com.epicdima.theatraxity.dal.mysql.builders.queries;

import com.epicdima.lib.dal.builders.SqlBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author EpicDima
 */
public final class MySqlInsertQuerySqlBuilder extends SqlBuilder {
    private static final String INSERT_INTO = "INSERT INTO";
    private static final String VALUES = "VALUES";
    private static final String PARAMETER_MARK = "?";

    private String tableName = null;
    private final List<String> insertableColumnNames = new ArrayList<>();

    public MySqlInsertQuerySqlBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public MySqlInsertQuerySqlBuilder addInsertableColumn(String columnName) {
        insertableColumnNames.add(columnName);
        return this;
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(INSERT_INTO).append(" ");
        builder.append(tableName).append(" ");
        builder.append("(").append("`").append(String.join("`, `", insertableColumnNames))
                .append("`").append(")").append(" ");
        builder.append(VALUES).append(" ");
        String[] parameters = new String[insertableColumnNames.size()];
        Arrays.fill(parameters, PARAMETER_MARK);
        builder.append("(").append(String.join(", ", parameters)).append(")").append(";");
        return builder.toString();
    }

    private void verify() {
        if (tableName == null) {
            throw new IllegalStateException("Table Name must be specified");
        }
    }
}
