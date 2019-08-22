package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by zengxf on 2019/8/1.
 */
@Getter
@AllArgsConstructor
public class SelectSql {

    private String sql;
    private Map<String, Object> param;

}
