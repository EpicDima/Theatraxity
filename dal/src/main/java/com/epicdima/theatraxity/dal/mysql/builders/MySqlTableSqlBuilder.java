package com.epicdima.theatraxity.dal.mysql.builders;

import com.epicdima.lib.dal.builders.ExecutableSqlBuilder;
import com.epicdima.lib.dal.other.ColumnConfigurator;
import com.epicdima.theatraxity.dal.mysql.builders.configurators.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EpicDima
 */
public final class MySqlTableSqlBuilder extends ExecutableSqlBuilder {
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static final String IF_NOT_EXISTS = "IF NOT EXISTS";

    private static final ColumnConfigurator[] configurators = {
            new MySqlNameColumnConfigurator(),
            new MySqlTypeColumnConfigurator(),
            new MySqlPrimaryKeyColumnConfigurator(),
            new MySqlNotNullColumnConfigurator(),
            new MySqlAutoIncrementColumnConfigurator()
    };

    private String tableName = null;
    private boolean createIfExists = false;
    private final List<Field> columns = new ArrayList<>();

    public MySqlTableSqlBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public MySqlTableSqlBuilder setCreateIfExists(boolean createIfExists) {
        this.createIfExists = createIfExists;
        return this;
    }

    public void addColumn(Field field) {
        columns.add(field);
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(CREATE_TABLE).append(" ");
        if (!createIfExists) {
            builder.append(IF_NOT_EXISTS).append(" ");
        }
        builder.append(tableName).append(" ").append("(");
        List<String> columnLines = new ArrayList<>(columns.size());
        for (Field columnField : columns) {
            String columnLine = "";
            for (ColumnConfigurator configurator : configurators) {
                columnLine = configurator.configure(columnField, columnLine) + " ";
            }
            columnLines.add(columnLine);
        }
        builder.append(String.join(", ", columnLines)).append(")").append(";");
        return builder.toString();
    }

    private void verify() {
        if (tableName == null) {
            throw new IllegalStateException("Table Name must be specified");
        }
    }
}
