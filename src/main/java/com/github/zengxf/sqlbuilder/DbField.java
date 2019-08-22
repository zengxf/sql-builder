package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created by zengxf on 2019/8/1.
 */
@Data
@AllArgsConstructor
public class DbField {

    public static final String
            NULL = "##",
            COUNT_ALL = "COUNT(*)";

    private String field;
    private String alias;

    public static DbField ofCount() {
        return of(COUNT_ALL, NULL);
    }

    public static DbField ofCount(String alias) {
        return of(COUNT_ALL, alias);
    }

    public static DbField of(String field) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        return of(field, NULL);
    }

    public static DbField of(String field, String alias) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        if (!Objects.equals(NULL, alias) && StringUtils.isEmpty(alias))
            throw SqlBuildException.of("别名不能为空");
        return new DbField(field, alias);
    }

    String toSql() {
        StringBuilder sb = new StringBuilder(32);
        sb.append(field);
        if (!Objects.equals(NULL, alias))
            sb.append(" AS ").append("`").append(alias).append("`");
        return sb.toString();
    }

}
