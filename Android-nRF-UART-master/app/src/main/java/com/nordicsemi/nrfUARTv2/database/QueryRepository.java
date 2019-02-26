package com.nordicsemi.nrfUARTv2.database;

import android.provider.BaseColumns;

/**
 * Created by WitLab on 2018-05-04.
 */

public final class QueryRepository {
    public static final String TABLENAME_LOGS = "logs";
    public static final String COL_DATETIME = "datetime";
    public static final String COL_UVI = "uvi";

    public static final class Create implements BaseColumns {

        public static final String QUERY_CREATE_TABLE_LOGS =
                "CREATE TABLE IF NOT EXISTS " + TABLENAME_LOGS + " (" +
                        COL_DATETIME + " TEXT PRIMARY KEY, " +
                        COL_UVI + " DOUBLE NOT NULL DEFAULT 0 " +
                ");";
    }

    public static final class Drop implements BaseColumns {

        public static final String QUERY_DROP_TABLE_LOGS =
                "DROP TABLE IF EXISTS " + TABLENAME_LOGS;
    }

    public static final class Select implements BaseColumns {

        public static final String QUERY_SELECT_LASTLOG =
                "SELECT * FROM " + TABLENAME_LOGS + " ORDER BY DATETIME DESC LIMIT 1;";

    }
}
