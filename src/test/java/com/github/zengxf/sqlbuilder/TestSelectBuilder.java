package com.github.zengxf.sqlbuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by zengxf on 2019/8/22.
 */
@Slf4j
public class TestSelectBuilder {

    @Test
    public void testSimplest() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testField() {
        SqlResult build = SelectBuilder.of()
                .addField("user_id")
                .addField("user_name", "name")
                .addField("user_name", "name_1")
                .addField(DbField.ofCount())
                .addField(DbField.ofCount())
                .table("user")
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testPage() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .page(10, 10)
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testSort() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .setSort(DbSort.of().desc("create_date"))
                .page(1, 10)
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testWhere() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("name", DbCriteriaType.LIKE, "test"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testGroup() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .addField("user_name", "name")
                .addField("user_age")
                .addField(DbField.ofCount("total"))
                .addGroup("user_name")
                .addGroup("user_age")
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("user_name", DbCriteriaType.LIKE, "test%"))
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

    @Test
    public void testWhereGroup() {
        SqlResult build = SelectBuilder.of()
                .table("user")
                .where(DbCriteriaGroup.ofAnd()
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                        .addItem(DbCriteria.of("name", DbCriteriaType.LIKE, "zxf"))
                        .addGroup(DbCriteriaGroup.ofOr()
                                .addItem(DbCriteria.of("age", DbCriteriaType.BETWEEN, 18, 28))
                                .addItem(DbCriteria.of("state", DbCriteriaType.EQ, "active"))
                                .addItem(DbCriteria.of("six", DbCriteriaType.IS_NULL))
                        )
                        .addGroup(DbCriteriaGroup.ofNot()
                                .addItem(DbCriteria.of("age", DbCriteriaType.BETWEEN, 18, 28))
                                .addItem(DbCriteria.of("state", DbCriteriaType.EQ, "active"))
                                .addItem(DbCriteria.of("six", DbCriteriaType.IS_NULL))
                        )
                )
                .build();
        log.info("sql: \n{}", build.getSql());
        log.info("param: {}", build.getParam());
    }

}
