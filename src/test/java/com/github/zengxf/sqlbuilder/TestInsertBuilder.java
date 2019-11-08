package com.github.zengxf.sqlbuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by zengxf on 2019/11/8.
 */
@Slf4j
public class TestInsertBuilder {

    @Test
    public void test() {
        SqlResult build = InsertBuilder.of()
                .table("user")
                .addValue("name", "zxf")
                .addValue("age", 22)
                .addLiteralValue("status", "1")
                .addLiteralValue("sign", "'test'")
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

}
