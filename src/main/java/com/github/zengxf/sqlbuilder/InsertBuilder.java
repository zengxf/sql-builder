package com.github.zengxf.sqlbuilder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zengxf on 2019/11/8.
 */
@Slf4j
@NoArgsConstructor(staticName = "of")
public class InsertBuilder extends AbstractBuilder {

    private List<DbSet> values = new ArrayList<>(4);


    public InsertBuilder table(String table) {
        super.setTable(table);
        return this;
    }

    public InsertBuilder addValue(String field, Object value) {
        values.add(DbSet.of(field, value));
        return this;
    }

    public InsertBuilder addLiteralValue(String field, String literal) {
        values.add(DbSet.ofLiteral(field, literal));
        return this;
    }

    @Override
    public SqlResult build() {
        Map<String, Object> param = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(100);
        sql.append("INSERT INTO ").append(super.validateTable());
        this.appendValue(param, sql);

        log.debug("build-sql: \n{}", sql);
        log.debug("build-param: {}", param);
        return new SqlResult(sql.toString(), param);
    }

    private void appendValue(Map<String, Object> param, StringBuilder sql) {
        if (values.isEmpty())
            throw SqlBuildException.of("value 集合为空");

        String fieldStr = values.stream()
                .map(DbSet::getField)
                .collect(Collectors.joining(", "));
        String valueStr = values.stream()
                .map(set -> {
                    String value;
                    if (set.isLiteral()) {
                        value = set.getParam().toString();
                    } else {
                        value = ":" + set.getField();
                        param.put(set.getField(), set.getParam());
                    }
                    return value;
                })
                .collect(Collectors.joining(", "));
        sql.append("(").append(fieldStr).append(")")
                .append(BR)
                .append("VALUE(").append(valueStr).append(")")
                .append(BR);
    }

}
