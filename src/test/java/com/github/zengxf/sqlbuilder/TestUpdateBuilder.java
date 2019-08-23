package com.github.zengxf.sqlbuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by zengxf on 2019/8/23.
 */
@Slf4j
public class TestUpdateBuilder {

    @Test
    public void testSimple() {
        SqlResult build = UpdateBuilder.of()
                .table("user")
                .addSet(DbSet.of("status", 1))
                .addSet(DbSet.ofLiteral("age", "age + 1"))
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test(expected = SqlBuildException.class)
    public void testError() {
        SqlResult build = UpdateBuilder.of()
                .table("user")
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testWhere() {
        SqlResult build = UpdateBuilder.of()
                .table("user")
                .addSet(DbSet.of("status", 1))
                .addSet(DbSet.ofLiteral("age", "age + 1"))
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("name", DbCriteriaType.LIKE, "test"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testJoin() {
        SqlResult build = UpdateBuilder.of()
                .table("user u")
                .addJoin(DbJoin.left("order o",
                        DbCriteria.ofLiteral("o.uid", DbCriteriaType.EQ, "u.id"),
                        DbCriteria.of("o.status", DbCriteriaType.EQ, 1)
                ))
                .addSet(DbSet.of("u.status", 1))
                .addSet(DbSet.ofLiteral("u.age", "age + 1"))
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("u.id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("u.name", DbCriteriaType.LIKE, "test"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }


}
