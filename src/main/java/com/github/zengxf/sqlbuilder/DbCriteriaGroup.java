package com.github.zengxf.sqlbuilder;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.zengxf.sqlbuilder.DbCriteriaGroup.RelationType.NOT;
import static com.github.zengxf.sqlbuilder.DbCriteriaGroup.RelationType.OR;

/**
 * Created by zengxf on 2019/8/1.
 */
@Getter
public class DbCriteriaGroup {

    private AtomicInteger sign;
    private RelationType type;
    private Set<DbCriteria> items = new LinkedHashSet<>(4);
    private Set<DbCriteriaGroup> groups = new LinkedHashSet<>(2);

    enum RelationType {
        AND, OR, NOT
    }

    public static DbCriteriaGroup ofAnd() {
        return of(RelationType.AND);
    }

    public static DbCriteriaGroup ofOr() {
        return of(RelationType.OR);
    }

    public static DbCriteriaGroup ofNot() {
        return of(RelationType.NOT);
    }

    private static DbCriteriaGroup of(RelationType type) {
        DbCriteriaGroup group = new DbCriteriaGroup();
        group.type = type;
        return group;
    }

    public DbCriteriaGroup addItem(DbCriteria item) {
        this.items.add(item);
        return this;
    }

    public DbCriteriaGroup addGroup(DbCriteriaGroup group) {
        this.groups.add(group);
        return this;
    }

    String toSql(int sign, Map<String, Object> param) {
        this.sign = new AtomicInteger(sign);
        return this.toSql(this, param);
    }

    private String toSql(DbCriteriaGroup group, Map<String, Object> param) {
        DbCriteriaGroup.RelationType relationType = group.getType();

        List<String> relationList = group.getItems().stream()
                .map(cri -> {
                    String field = cri.getField();
                    DbCriteriaType type = cri.getType();
                    String operator = type.operator;
                    if (type.params == 1) {
                        String key = String.format("%s_%s", field, sign);
                        param.put(key, cri.getParams()[0]);
                        operator = String.format(operator, ":" + key);
                    } else if (type.params == 2) {
                        String key1 = String.format("%s_%s_1", field, sign);
                        param.put(key1, cri.getParams()[0]);
                        String key2 = String.format("%s_%s_2", field, sign);
                        param.put(key2, cri.getParams()[1]);
                        operator = String.format(operator, ":" + key1, ":" + key2);
                    }
                    return field + " " + operator;
                })
                .collect(Collectors.toList());

        group.getGroups().stream()
                .map(childGroup -> {
                    sign.addAndGet(10);
                    return this.toSql(childGroup, param);
                })
                .forEach(relationList::add);

        String relation = relationType == OR ? " OR " : " AND "; // NOT 的话，内部也是用 AND
        String temp = relationList.stream().collect(Collectors.joining(relation));
        String prefix = relationType == NOT ? "!" : "";
        return String.format("%s(%s)", prefix, temp);
    }

}
