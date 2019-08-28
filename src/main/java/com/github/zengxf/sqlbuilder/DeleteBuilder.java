package com.github.zengxf.sqlbuilder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DELETE 语句构造器
 * <p>
 * Created by zengxf on 2019/8/28.
 */
@Slf4j
@NoArgsConstructor(staticName = "of")
public class DeleteBuilder extends AbstractBuilder {

    private List<String> delTables = new ArrayList<>(2);


    public DeleteBuilder table(String table) {
        super.table = table;
        this.validateTable();
        return this;
    }

    public DeleteBuilder addDeleteTable(String table) {
        if (StringUtils.isEmpty(table))
            throw SqlBuildException.of("表名不能为空");
        delTables.add(table);
        return this;
    }

    public DeleteBuilder addJoin(DbJoin join) {
        super.putJoin(join);
        return this;
    }

    public DeleteBuilder where(DbCriteriaGroup criteria) {
        this.where = criteria;
        return this;
    }

    @Override
    public SqlResult build() {
        Map<String, Object> param = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(100);

        if (super.joins.isEmpty()) {
            sql.append("DELETE FROM ").append(super.validateTable()).append(BR);
        } else {
            sql.append("DELETE ").append(super.validateTable());
            this.delTables.forEach(del -> sql.append(", ").append(del));
            sql.append(BR);
            sql.append("FROM ").append(super.validateTable()).append(BR);
            this.appendJoin(DELETE_JOIN_SIGN, param, sql);
        }
        super.appendWhere(DELETE_WHERE_SIGN, param, sql);

        log.debug("build-sql: \n{}", sql);
        log.debug("build-param: {}", param);
        return new SqlResult(sql.toString(), param);
    }

}
