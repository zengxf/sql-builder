package com.github.zengxf.sqlbuilder;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.zengxf.sqlbuilder.DbCriteriaGroup.RelationType.NOT;
import static com.github.zengxf.sqlbuilder.DbCriteriaGroup.RelationType.OR;

/**
 * Created by zengxf on 2019/8/1.
 */
@Getter
public class DbCriteriaGroup implements SqlConstant {

    private AtomicInteger sign;
    private RelationType type;
    private Set<DbCriteria> items = new LinkedHashSet<>(4);
    private Set<DbCriteriaGroup> groups = new LinkedHashSet<>(2);

    public enum RelationType {
        AND, OR, NOT
    }


    public static DbCriteriaGroup ofAnd(DbCriteria... items) {
        DbCriteriaGroup group = of(RelationType.AND);
        Stream.of(items).forEach(group::addItem);
        return group;
    }

    public static DbCriteriaGroup ofOr(DbCriteria... items) {
        DbCriteriaGroup group = of(RelationType.OR);
        Stream.of(items).forEach(group::addItem);
        return group;
    }

    public static DbCriteriaGroup ofNot(DbCriteria... items) {
        DbCriteriaGroup group = of(RelationType.NOT);
        Stream.of(items).forEach(group::addItem);
        return group;
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

    public boolean isEmpty() {
        return items.isEmpty() && groups.isEmpty();
    }

    String toSql(int sign, Map<String, Object> param) {
        this.validateEmbed();
        this.sign = new AtomicInteger(sign);
        return this.toSql(this, param);
    }


    void validateEmbed() {
        Set<DbCriteriaGroup> where = new HashSet<>();
        where.add(this);
        this.validateEmbed(where, this.groups);
    }

    private void validateEmbed(Set<DbCriteriaGroup> exist, Set<DbCriteriaGroup> temps) {
        if (temps == null || temps.isEmpty())
            return;
        temps.forEach(temp -> {
            if (exist.contains(temp))
                throw SqlBuildException.of("条件存在重复嵌套");
            exist.add(temp);
            this.validateEmbed(exist, temp.groups);
        });
    }

    private String toSql(DbCriteriaGroup group, Map<String, Object> param) {
        DbCriteriaGroup.RelationType relationType = group.getType();

        List<String> relationList = group.getItems().stream()
                .map(cri -> {
                    String field = cri.getField();
                    DbCriteriaType type = cri.getType();
                    String operator = type.operator;
                    boolean literal = cri.isLiteral();
                    Object[] params = cri.getParams();
                    if (type.params == 1) {
                        String key = params[0].toString();
                        if (!literal) {
                            String _key = String.format("%s_%s", field, sign);
                            key = ":" + _key;
                            param.put(_key, params[0]);
                        }
                        operator = String.format(operator, key);
                    } else if (type.params == 2) {
                        String key1 = params[0].toString();
                        String key2 = params[1].toString();
                        if (!literal) {
                            String _key1 = String.format("%s_%s_1", field, sign);
                            String _key2 = String.format("%s_%s_2", field, sign);
                            key1 = ":" + _key1;
                            key2 = ":" + _key2;
                            param.put(_key1, params[0]);
                            param.put(_key2, params[1]);
                        }
                        operator = String.format(operator, key1, key2);
                    }
                    return field + " " + operator;
                })
                .collect(Collectors.toList());

        group.getGroups().stream()
                .map(childGroup -> {
                    sign.addAndGet(10);
                    return BR + TR + TR + this.toSql(childGroup, param);
                })
                .forEach(relationList::add);

        String relation = relationType == OR ? " OR " : " AND "; // NOT 的话，内部也是用 AND
        String temp = relationList.stream().collect(Collectors.joining(relation));
        String prefix = relationType == NOT ? "!" : "";
        return String.format("%s(%s)", prefix, temp);
    }

}
