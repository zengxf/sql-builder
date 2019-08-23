package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zengxf on 2019/8/23.
 */
@Data
@AllArgsConstructor
public class DbSet {

    private String field;
    private boolean literal;
    private Object param;

    public static DbSet of(String field, Object param) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        return new DbSet(field, false, param);
    }

    public static DbSet ofLiteral(String field, String literal) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        if (StringUtils.isEmpty(literal))
            throw SqlBuildException.of("字面量不能为空");
        return new DbSet(field, true, literal);
    }

}
