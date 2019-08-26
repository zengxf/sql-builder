package com.github.zengxf.sqlbuilder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SELECT 语句构造器
 * <p>
 * Created by zengxf on 2019/8/1.
 */
@Slf4j
@NoArgsConstructor(staticName = "of")
public class SelectBuilder extends AbstractBuilder {

    private Set<DbField> fields = new LinkedHashSet<>();
    private Set<String> groups = new LinkedHashSet<>();
    private DbSort sort;
    private Integer pageIndex;
    private Integer pageSize;


    public SelectBuilder table(String table) {
        super.setTable(table);
        return this;
    }

    public SelectBuilder addFields(String... fields) {
        Stream.of(fields).forEach(this::addField);
        return this;
    }

    public SelectBuilder addField(String field) {
        return this.addField(DbField.of(field));
    }

    public SelectBuilder addField(String field, String alias) {
        return this.addField(DbField.of(field, alias));
    }

    public SelectBuilder addField(DbField field) {
        if (field == null)
            throw SqlBuildException.of("字段为空");
        fields.add(field);
        return this;
    }

    public SelectBuilder addJoin(DbJoin join) {
        super.putJoin(join);
        return this;
    }

    public SelectBuilder addGroup(String group) {
        if (StringUtils.isEmpty(group))
            throw SqlBuildException.of("分组字段为空");
        groups.add(group);
        return this;
    }

    public SelectBuilder where(DbCriteriaGroup criteria) {
        this.where = criteria;
        return this;
    }

    /*** 逻辑运算符默认用 AND */
    public SelectBuilder where(DbCriteria... items) {
        this.where = DbCriteriaGroup.ofAnd(items);
        return this;
    }

    public SelectBuilder setSort(DbSort sort) {
        if (sort == null || sort.isEmpty())
            throw SqlBuildException.of("排序没有指定");
        this.sort = sort;
        return this;
    }

    /*** 只复制表名和条件 */
    public SelectBuilder copy() {
        return SelectBuilder.of()
                .table(table)
                .where(where);
    }

    /*** 只复制表名和条件 */
    public SelectBuilder copyForCount() {
        return this.copy()
                .addField(DbField.ofCount());
    }


    private int pageIndex() {
        if (pageIndex == null) {
            log.warn("页索引为空，将返回 0");
            return 0;
        }
        if (pageIndex < 0) {
            log.warn("页索引（当前[{}]）小于 0，将返回 0", pageIndex);
            return 0;
        }
        return pageIndex;
    }

    private int validatePageSize() {
        if (pageSize < 1)
            throw SqlBuildException.of("页大小不能小于 1，当前是：[%d]", pageSize);
        return pageSize;
    }

    public SelectBuilder page(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        if (pageSize != null) {
            this.pageSize = pageSize;
            this.validatePageSize();
        }
        return this;
    }


    @Override
    public SqlResult build() {
        Map<String, Object> param = new LinkedHashMap<>();
        StringBuilder sql = new StringBuilder(100);

        sql.append("SELECT").append(BR);
        this.appendField(sql);
        sql.append("FROM ").append(super.validateTable()).append(BR);
        this.appendJoin(SELECT_JOIN_SIGN, param, sql);
        super.appendWhere(SELECT_WHERE_SIGN, param, sql);
        this.appendGroup(sql);
        this.appendSort(sql);
        this.appendPage(param, sql);

        log.debug("build-sql: \n{}", sql);
        log.debug("build-param: {}", param);
        return new SqlResult(sql.toString(), param);
    }


    private void appendField(StringBuilder sql) {
        if (fields.isEmpty()) {
            log.warn("没有指定字段，默认将使用 `*`");
            sql.append(TR).append("*").append(BR);
        } else {
            String temp = fields.stream().map(DbField::toSql).collect(Collectors.joining(", "));
            sql.append(TR).append(temp).append(BR);
        }
    }

    private void appendGroup(StringBuilder sql) {
        if (groups.isEmpty())
            return;
        sql.append(TR).append("GROUP BY ");
        String temp = groups.stream().collect(Collectors.joining(", "));
        sql.append(temp).append(BR);
    }

    private void appendSort(StringBuilder sql) {
        if (sort == null)
            return;
        String temp = sort.get()
                .map(order ->
                        String.format("%s %s", order.getProperty(), order.getDirection())
                ).collect(Collectors.joining(", "));
        sql.append("ORDER BY ").append(temp).append(BR);
    }

    private void appendPage(Map<String, Object> param, StringBuilder sql) {
        if (pageSize == null)
            return;
        sql.append("LIMIT :pageStart, :pageSize");
        param.put("pageStart", this.pageIndex() * this.validatePageSize());
        param.put("pageSize", this.validatePageSize());
    }

}
