package com.github.zengxf.sqlbuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by zengxf on 2019/8/23.
 */
@Slf4j
public class TestDeleteBuilder {

    @Test
    public void testWhere() {
        SqlResult build = DeleteBuilder.of()
                .table("user")
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
        SqlResult build = DeleteBuilder.of()
                .table("user")
                .addJoin(DbJoin.left("order",
                        DbCriteria.ofLiteral("order.uid", DbCriteriaType.EQ, "user.id"),
                        DbCriteria.of("order.status", DbCriteriaType.EQ, 1)
                ))
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("user.name", DbCriteriaType.LIKE, "test"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testJoinDeleteMulti() {
        SqlResult build = DeleteBuilder.of()
                .table("user")
                .addDeleteTable("order")
                .addJoin(DbJoin.left("order",
                        DbCriteria.ofLiteral("order.uid", DbCriteriaType.EQ, "user.id"),
                        DbCriteria.of("order.status", DbCriteriaType.EQ, 1)
                ))
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("user.name", DbCriteriaType.LIKE, "test"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

}
