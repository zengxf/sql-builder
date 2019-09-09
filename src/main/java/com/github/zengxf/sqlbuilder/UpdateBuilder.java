package com.github.zengxf.sqlbuilder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UPDATE 语句构造器
 * <p>
 * Created by zengxf on 2019/8/23.
 */
@Slf4j
@NoArgsConstructor(staticName = "of")
public class UpdateBuilder extends AbstractBuilder {

    private List<DbSet> setList = new ArrayList<>(4);


    public UpdateBuilder table(String table) {
        super.table = table;
        this.validateTable();
        return this;
    }

    public UpdateBuilder addJoin(DbJoin join) {
        super.putJoin(join);
        return this;
    }

    public UpdateBuilder where(DbCriteriaGroup criteria) {
        this.where = criteria;
        return this;
    }

    public UpdateBuilder addSet(DbSet set) {
        if (set == null)
            throw SqlBuildException.of("set 设置为空");
        setList.add(set);
        return this;
    }

    @Override
    public SqlResult build() {
        this.validateEmbed();

        Map<String, Object> param = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(100);
        sql.append("UPDATE ").append(super.validateTable()).append(BR);
        this.appendJoin(UPDATE_JOIN_SIGN, param, sql);
        this.appendSet(param, sql);
        super.appendWhere(UPDATE_WHERE_SIGN, param, sql);

        log.debug("build-sql: \n{}", sql);
        log.debug("build-param: {}", param);
        return new SqlResult(sql.toString(), param);
    }


    private void validateEmbed() {
        if (where != null)
            where.validateEmbed();
    }

    private void appendSet(Map<String, Object> param, StringBuilder sql) {
        if (setList.isEmpty())
            throw SqlBuildException.of("set 设置集合为空");
        String setStr = setList.stream()
                .map(set -> {
                    String value;
                    if (set.isLiteral()) {
                        value = set.getParam().toString();
                    } else {
                        value = ":" + set.getField();
                        param.put(set.getField(), set.getParam());
                    }
                    return String.format("%s = %s", set.getField(), value);
                })
                .collect(Collectors.joining(", "));
        sql.append(TR).append("SET ").append(setStr).append(BR);
    }

}
