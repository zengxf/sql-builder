# SQL-Builder
- SQL 构建器，用于 Spring-JDBC `NamedParameterJdbcTemplate`
  - SQL 格式：`MySQL`
- 环境要求：
  - JDK：11

## 示例
### 使用 NamedParameterJdbcTemplate
```
SqlResult sql = SelectBuilder.of()
        .table("note_review")
        .addField("DATE_FORMAT(review_date, '%Y-%m-%d')", "dt")
        .where(DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("user_id", DbCriteriaType.EQ, userId))
                .addItem(DbCriteria.of("status", DbCriteriaType.EQ, NoteReviewStatusEnum.INITIAL.code))
                .addItem(DbCriteria.of("review_date", DbCriteriaType.BETWEEN, DateUtils.lastMonth()))
        )
        .addSort(new Sort(Sort.Direction.DESC, "dt"))
        .page(0, 1)
        .build();
List<String> list = super.namedJdbc.queryForList(sql.getSql(), sql.getParam(), String.class);
return ListUtils.one(list);
```

### SELECT
#### 01 分页
- Java Code
```
SelectBuilder builder = SelectBuilder.of()
        .table("user")
        .addField("user_name", "name")
        .addField("user_age")
        .where(DbCriteria.of("id", DbCriteriaType.EQ, 10))
        .setSort(DbSort.of().desc("create_date"))
        .page(1, 10);

SqlResult build = builder.build();
log.info("list-sql: \n{}", build.getSql());
log.info("list-param: {}", build.getParam());

build = builder.copyForCount().build();
log.info("count-sql: \n{}", build.getSql());
log.info("count-param: {}", build.getParam());
```
- 输出
```
[main] INFO TestSelectBuilder - list-sql: 
SELECT
  user_name AS `name`, user_age
FROM user
  WHERE (id = :id_1000)
ORDER BY create_date DESC
LIMIT :pageStart, pageSize
[main] INFO TestSelectBuilder - list-param: {id_1000=10, pageStart=10, pageSize=10}

[main] INFO TestSelectBuilder - count-sql: 
SELECT
  COUNT(*)
FROM user
  WHERE (id = :id_1000)

[main] INFO TestSelectBuilder - count-param: {id_1000=10}
```

#### 02 WHERE
- Java Code
```
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
```
- 输出
```
[main] WARN SelectBuilder - 没有指定字段，默认将使用 `*`
[main] INFO TestSelectBuilder - sql: 
SELECT
  *
FROM user
  WHERE (id = :id_1000 AND name LIKE '%' :name_1000 '%' AND 
    (age BETWEEN :age_1010_1 AND :age_1010_2 OR state = :state_1010 OR six IS NULL) AND 
    !(age BETWEEN :age_1020_1 AND :age_1020_2 AND state = :state_1020 AND six IS NULL))

[main] INFO TestSelectBuilder - param: {id_1000=10, name_1000=zxf, age_1010_1=18, age_1010_2=28, state_1010=active, 
    age_1020_1=18, age_1020_2=28, state_1020=active}
```

#### 03 JOIN
- Java Code
```
SqlResult build = SelectBuilder.of()
        .table("user u")
        .addField("u.name")
        .addField("o.date")
        .addJoin(DbJoin.left("order o", DbCriteriaGroup.ofAnd(
                DbCriteria.ofLiteral("o.uid", DbCriteriaType.EQ, "u.id"),
                DbCriteria.of("o.status", DbCriteriaType.EQ, 1)
        )))
        .where(DbCriteria.of("u.id", DbCriteriaType.EQ, 10))
        .build();
log.info("sql: \n{}", build.getSql());
log.info("param: {}", build.getParam());
```
- 输出
```
[main] INFO TestSelectBuilder - sql: 
SELECT
  u.name, o.date
FROM user u
  LEFT JOIN order o ON (o.uid = u.id AND o.status = :o.status_2000)
  WHERE (u.id = :u.id_1000)

[main] INFO TestSelectBuilder - param: {o.status_2000=1, u.id_1000=10}
```

### UPDATE
#### 01 Simple
- Java Code
```
SqlResult build = UpdateBuilder.of()
        .table("user")
        .addSet(DbSet.of("status", 1))
        .addSet(DbSet.ofLiteral("age", "age + 1"))
        .build();
log.info("sql: \n{}", build.getSql());
log.info("param: {}", build.getParam());
```
- 输出
```
[main] INFO TestUpdateBuilder - sql: 
UPDATE user
  SET status = :status, age = age + 1

[main] INFO TestUpdateBuilder - param: {status=1}
```

#### 02 WHERE
- Java Code
```
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
```
- 输出
```
[main] INFO TestUpdateBuilder - sql: 
UPDATE user
  SET status = :status, age = age + 1
  WHERE (id = :id_5000 AND name LIKE '%' :name_5000 '%')

[main] INFO TestUpdateBuilder - param: {status=1, id_5000=10, name_5000=test}
```

#### 03 JOIN
- Java Code
```
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
```
- 输出
```
[main] INFO TestUpdateBuilder - sql: 
UPDATE user u
  LEFT JOIN order o ON (o.uid = u.id AND o.status = :o.status_6000)
  SET u.status = :u.status, u.age = age + 1
  WHERE (u.id = :u.id_5000 AND u.name LIKE '%' :u.name_5000 '%')

[main] INFO TestUpdateBuilder - param: {o.status_6000=1, u.status=1, u.id_5000=10, u.name_5000=test}
```


### DELETE
#### 01 Simple
- Java Code
```
SqlResult build = DeleteBuilder.of()
        .table("user")
        .where(DbCriteriaGroup.ofAnd()
                .addItem(DbCriteria.of("id", DbCriteriaType.EQ, 10))
                .addItem(DbCriteria.of("name", DbCriteriaType.LIKE, "test"))
        )
        .build();
log.info("sql: \n{}", build.getSql());
log.info("param: {}", build.getParam());
```
- 输出
```
[main] INFO TestDeleteBuilder - sql: 
DELETE FROM user
  WHERE (id = :id_7000 AND name LIKE '%' :name_7000 '%')

[main] INFO TestDeleteBuilder - param: {id_7000=10, name_7000=test}
```

#### 02 JOIN
- Java Code
```
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
```
- 输出
```
[main] INFO TestDeleteBuilder - sql: 
DELETE user, order
FROM user
  LEFT JOIN order ON (order.uid = user.id AND order.status = :order.status_8000)
  WHERE (user.id = :user.id_7000 AND user.name LIKE '%' :user.name_7000 '%')

[main] INFO TestDeleteBuilder - param: {order.status_8000=1, user.id_7000=10, user.name_7000=test}
```


## TODO
- [x] DELETE
- [ ] SELECT - `WITH AS`
- [ ] JOIN 子查询
- [ ] JOIN 死循环嵌套检查

## 使用者
- 1 https://www.jjnote.cn