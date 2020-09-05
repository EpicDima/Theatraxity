package com.epicdima.theatraxity.dal.mysql.dao;

import com.epicdima.lib.dal.dao.BaseDaoImpl;
import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.mysql.builders.queries.MySqlDeleteQuerySqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.queries.MySqlInsertQuerySqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.queries.MySqlSelectQuerySqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.queries.MySqlUpdateQuerySqlBuilder;

/**
 * @author EpicDima
 */
public abstract class MySqlBaseDao<D, T> extends BaseDaoImpl<D, T> {

    protected final Mapper<D, T> to;
    protected final Mapper<T, D> from;

    public MySqlBaseDao(ConnectionPool connectionPool, Mapper<D, T> to, Mapper<T, D> from) {
        super(connectionPool);
        this.to = to;
        this.from = from;
    }

    @Override
    protected String createSelectSqlQuery() {
        return new MySqlSelectQuerySqlBuilder()
                .setTableName(tableName)
                .createQuery();
    }

    @Override
    protected String createSelectByColumnSqlQuery(String columnName) {
        return new MySqlSelectQuerySqlBuilder()
                .setTableName(tableName)
                .addFilterableColumn(columnName)
                .createQuery();
    }

    @Override
    protected String createSelectByTwoColumnSqlQuery(String firstColumnName, String secondColumnName) {
        return new MySqlSelectQuerySqlBuilder()
                .setTableName(tableName)
                .addFilterableColumn(firstColumnName)
                .addFilterableColumn(secondColumnName)
                .createQuery();
    }

    @Override
    protected String createInsertSqlQuery() {
        MySqlInsertQuerySqlBuilder builder = new MySqlInsertQuerySqlBuilder();
        builder.setTableName(tableName);
        for (String columnName : fields.keySet()) {
            if (!columnName.equals(primaryColumn)) {
                builder.addInsertableColumn(columnName);
            }
        }
        return builder.createQuery();
    }

    @Override
    protected String createUpdateSqlQuery() {
        MySqlUpdateQuerySqlBuilder builder = new MySqlUpdateQuerySqlBuilder();
        builder.setTableName(tableName);
        for (String columnName : fields.keySet()) {
            if (columnName.equals(primaryColumn)) {
                builder.addFilterableColumn(primaryColumn);
            } else {
                builder.addUpdateableColumn(columnName);
            }
        }
        return builder.createQuery();
    }

    @Override
    protected String createDeleteSqlQuery() {
        return new MySqlDeleteQuerySqlBuilder()
                .setTableName(tableName)
                .addFilterableColumn(primaryColumn)
                .createQuery();
    }

    @Override
    protected T toModel(D item) {
        if (item == null) {
            return null;
        }
        return to.map(item);
    }

    @Override
    protected D fromModel(T item) {
        return from.map(item);
    }
}
