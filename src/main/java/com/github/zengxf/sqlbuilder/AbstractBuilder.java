package com.github.zengxf.sqlbuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zengxf on 2019/8/23.
 */
public abstract class AbstractBuilder implements SqlConstant {

    protected String table;
    protected List<DbJoin> joins = new ArrayList<>(2);
    protected DbCriteriaGroup where;


    public abstract SqlResult build();


    protected void setTable(String table) {
        this.table = table;
        this.validateTable();
    }

    protected String validateTable() {
        if (StringUtils.isEmpty(table))
            throw SqlBuildException.of("表名不能为空");
        return table;
    }

    protected void putJoin(DbJoin join) {
        if (join == null)
            throw SqlBuildException.of("JOIN 为空");
        joins.add(join);
    }


    protected void appendJoin(int joinSign, Map<String, Object> param, StringBuilder sql) {
        if (joins.isEmpty())
            return;
        AtomicInteger sign = new AtomicInteger(joinSign);
        joins.forEach(join -> {
            String joinSql = join.toSql(sign.getAndAdd(100), param);
            sql.append(TR).append(joinSql).append(BR);
        });
    }

    protected void appendWhere(int whereSign, Map<String, Object> param, StringBuilder sql) {
        if (where == null)
            return;
        sql.append(TR).append("WHERE ");
        String temp = where.toSql(whereSign, param);
        sql.append(temp).append(BR);
    }

}
