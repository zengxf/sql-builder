package com.github.zengxf.sqlbuilder;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static com.github.zengxf.sqlbuilder.DbCriteriaType.*;

/**
 * Created by zengxf on 2019/8/1.
 */
@Data
public class DbCriteria {

    private String field;
    private DbCriteriaType type;
    private boolean literal;
    private Object[] params;


    public static DbCriteria lt(String field, Object param) {
        return of(field, LT, param);
    }

    public static DbCriteria lte(String field, Object param) {
        return of(field, LTE, param);
    }

    public static DbCriteria gt(String field, Object param) {
        return of(field, GT, param);
    }

    public static DbCriteria gte(String field, Object param) {
        return of(field, GTE, param);
    }

    public static DbCriteria eq(String field, Object param) {
        return of(field, EQ, param);
    }

    public static DbCriteria ne(String field, Object param) {
        return of(field, NE, param);
    }

    public static DbCriteria like(String field, Object param) {
        return of(field, LIKE, param);
    }

    public static DbCriteria between(String field, Object param1, Object param2) {
        return of(field, BETWEEN, param1, param2);
    }

    public static DbCriteria isNull(String field) {
        return of(field, IS_NULL);
    }

    public static DbCriteria isNotNull(String field) {
        return of(field, IS_NOT_NULL);
    }

    public static DbCriteria of(String field, DbCriteriaType type, Object... params) {
        return of(field, type, false, params);
    }

    public static DbCriteria ofLiteral(String field, DbCriteriaType type, Object... params) {
        return of(field, type, true, params);
    }

    private static DbCriteria of(String field, DbCriteriaType type, boolean literal, Object... params) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        if (type == null)
            throw SqlBuildException.of("条件类型不能为空");
        if (params.length != type.params)
            throw SqlBuildException.of("参数个数不对，期望参数个数[%d]", type.params);

        DbCriteria criteria = new DbCriteria();
        criteria.field = field;
        criteria.type = type;
        criteria.literal = literal;
        criteria.params = params;

        return criteria;
    }

}
