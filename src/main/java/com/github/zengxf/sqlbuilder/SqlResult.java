package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by zengxf on 2019/8/1.
 */
@Getter
@AllArgsConstructor
public class SqlResult {

    public final String sql;
    public final Map<String, Object> param;

}
