package com.github.zengxf.sqlbuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by zengxf on 2019/9/9.
 */
@Slf4j
public class TestDbCriteriaGroup {

    @Test
    public void validateEmbed() {
        DbCriteriaGroup g1 = DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                .addItem(DbCriteria.of("user.name", DbCriteriaType.LIKE, "test"));
        DbCriteriaGroup g2 = DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                .addGroup(g1);
        g2.validateEmbed();
        log.info("where: \n{}", g2.toSql(0, new HashMap<>()));
    }

    @Test
    public void testIn() {
        DbCriteriaGroup g1 = DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user.id", DbCriteriaType.IN, 10));
        HashMap<String, Object> param = new HashMap<>();
        log.info("where: \n{}", g1.toSql(0, param));
        log.info("param: \n{}", param);
    }

    @Test(expected = SqlBuildException.class)
    public void validateEmbedError() {
        DbCriteriaGroup g1 = DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                .addItem(DbCriteria.of("user.name", DbCriteriaType.LIKE, "test"));
        DbCriteriaGroup g2 = DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user.id", DbCriteriaType.EQ, 10))
                .addGroup(g1);
        g1.addGroup(g2);
        g2.validateEmbed();
        log.info("where: \n{}", g2.toSql(0, new HashMap<>()));
    }

}