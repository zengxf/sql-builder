package com.github.zengxf.sqlbuilder;

/**
 * Created by zengxf on 2019/8/9.
 */
public interface SqlConstant {

    int
            SELECT_WHERE_SIGN = 1000,
            SELECT_JOIN_SIGN = 2000,
            UPDATE_WHERE_SIGN = 5000,
            UPDATE_JOIN_SIGN = 6000;

    String
            BR = "\n",
            TR = "  ";

}
