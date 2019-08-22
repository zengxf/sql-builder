package com.github.zengxf.sqlbuilder;

/**
 * Created by zengxf on 2019/8/1.
 */
public class SqlBuildException extends RuntimeException {

    public final String errorMessage;
    public final Object[] params;

    public SqlBuildException(String errorMessage, Object[] params) {
        super(String.format(errorMessage, params));
        this.errorMessage = errorMessage;
        this.params = params;
    }

    public static SqlBuildException of(String errInfo, Object... params) {
        return new SqlBuildException(errInfo, params);
    }

}
