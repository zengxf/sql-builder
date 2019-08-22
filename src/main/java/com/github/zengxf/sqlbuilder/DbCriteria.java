package com.github.zengxf.sqlbuilder;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zengxf on 2019/8/1.
 */
@Data
public class DbCriteria {

    private String field;
    private DbCriteriaType type;
    private Object[] params;

    public static DbCriteria of(String field, DbCriteriaType type, Object... params) {
        if (StringUtils.isEmpty(field))
            throw SqlBuildException.of("字段不能为空");
        if (type == null)
            throw SqlBuildException.of("条件类型不能为空");
        if (params.length != type.params)
            throw SqlBuildException.of("参数个数不对，期望参数个数[%d]", type.params);

        DbCriteria criteria = new DbCriteria();
        criteria.field = field;
        criteria.type = type;
        criteria.params = params;

        return criteria;
    }

}
