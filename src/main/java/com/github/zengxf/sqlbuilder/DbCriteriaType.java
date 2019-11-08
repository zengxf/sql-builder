package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;

/**
 * Created by zengxf on 2019/8/1.
 */
@AllArgsConstructor
public enum DbCriteriaType {

    LT("< %s"),
    LTE("<= %s"),
    GT("> %s"),
    GTE(">= %s"),
    EQ("= %s"),
    NE("!= %s"),
    IN("IN (%s)"),
    LIKE("LIKE '%%' %s '%%'"), // 例："LIKE '%' :username '%'"；`:username` 两边一定要有空格
    LIKE_CI("LIKE '%%' %s '%%' COLLATE %s", 2), // 大小写不敏感
    LIKE_CI_U8("LIKE '%%' %s '%%' COLLATE utf8mb4_general_ci"), // 大小写不敏感
    LLIKE("LIKE '%%' %s"), // 左边模糊查询
    RLIKE("LIKE %s '%%'"), // 右边模糊查询
    BETWEEN("BETWEEN %s AND %s", 2),
    IS_NULL("IS NULL", 0),
    IS_NOT_NULL("IS NOT NULL", 0),
    ;

    public final String operator;
    public final int params;

    DbCriteriaType(String operator) {
        this(operator, 1);
    }

}
