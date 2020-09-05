package com.epicdima.theatraxity.dal.mysql.builders.queries;

import com.epicdima.lib.dal.builders.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author EpicDima
 */
public final class MySqlSelectQuerySqlBuilder extends SqlBuilder {
    private static final String SELECT = "SELECT";
    private static final String ALL = "*";
    private static final String FROM = "FROM";
    private static final String WHERE = "WHERE";
    private static final String AND = "AND";
    private static final String PARAMETER_MARK = "= ?";

    private String tableName = null;
    private final List<String> selectableColumnNames = new ArrayList<>();
    private final List<String> filterableColumnNames = new ArrayList<>();

    public MySqlSelectQuerySqlBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public MySqlSelectQuerySqlBuilder addSelectableColumn(String columnName) {
        selectableColumnNames.add(columnName);
        return this;
    }

    public MySqlSelectQuerySqlBuilder addFilterableColumn(String columnName) {
        filterableColumnNames.add(columnName);
        return this;
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(SELECT).append(" ");
        if (selectableColumnNames.isEmpty()) {
            builder.append(ALL).append(" ");
        } else {
            builder.append("(").append("`").append(String.join("`, `", selectableColumnNames))
                    .append("`").append(")").append(" ");
        }
        builder.append(FROM).append(" ");
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
