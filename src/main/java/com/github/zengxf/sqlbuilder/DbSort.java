package com.github.zengxf.sqlbuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by zengxf on 2019/8/22.
 */
@NoArgsConstructor(staticName = "of")
public class DbSort {

    private List<Order> orders = new ArrayList<>(2);


    public Stream<Order> get() {
        return orders.stream();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public DbSort asc(String... properties) {
        for (String property : properties) {
            this.orders.add(new Order(Direction.ASC, property));
        }
        return this;
    }

    public DbSort desc(String... properties) {
        for (String property : properties) {
            this.orders.add(new Order(Direction.DESC, property));
        }
        return this;
    }


    @Getter
    @ToString
    @AllArgsConstructor
    public static class Order {
        private final Direction direction;
        private final String property;
    }


    public enum Direction {
        ASC, DESC;
    }

}
