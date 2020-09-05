package com.epicdima.lib.dal.builders;

import com.epicdima.lib.dal.executors.SqlExecutor;

/**
 * @author EpicDima
 */
public abstract class SqlBuilder {

    public abstract String createQuery();

    protected SqlExecutor build() {
        throw new UnsupportedOperationException();
    }
}
