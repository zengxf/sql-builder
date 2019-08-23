package com.github.zengxf.sqlbuilder;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by zengxf on 2019/8/23.
 */
@Getter
public class DbJoin {

    private Type type;
    private String table;
    private DbCriteriaGroup on;

    public enum Type {
        LEFT, RIGHT, INNER, FULL
    }


    public static DbJoin left(String table, DbCriteria... onItems) {
        return left(table, DbCriteriaGroup.ofAnd(onItems));
    }

    public static DbJoin left(String table, DbCriteriaGroup on) {
        return of(Type.LEFT, table, on);
    }

    public static DbJoin right(String table, DbCriteria... onItems) {
        return right(table, DbCriteriaGroup.ofAnd(onItems));
    }

    public static DbJoin right(String table, DbCriteriaGroup on) {
        return of(Type.RIGHT, table, on);
    }


    public static DbJoin inner(String table, DbCriteria... onItems) {
        return inner(table, DbCriteriaGroup.ofAnd(onItems));
    }

    public static DbJoin inner(String table, DbCriteriaGroup on) {
        return of(Type.INNER, table, on);
    }


    private static DbJoin of(Type type, String table, DbCriteriaGroup on) {
        if (StringUtils.isEmpty(table))
            throw SqlBuildException.of("JOIN 的表名不能为空");
        if (on == null || on.isEmpty())
            throw SqlBuildException.of("JOIN 的 ON 条件不能为空");
        DbJoin join = new DbJoin();
        join.type = type;
        join.table = table;
        join.on = on;
        return join;
    }

    String toSql(int sign, Map<String, Object> param) {
        return type + " JOIN " + table + " ON " + on.toSql(sign, param);
    }

}
