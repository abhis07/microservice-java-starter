package com.github.starter.app.item.endpoints;

import com.github.starter.app.item.model.Item;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Items {
    private Items() {
    }

    public static Item createOne(String id, ZonedDateTime dateTime) {
        return new Item(id, "IPHONE 10", "used phone", "5000",
                "user",dateTime, dateTime);
    }

    public static Item createOne(ZonedDateTime dateTime) {
        return new Item("1", "IPHONE 10","used phone","5000","user",
                dateTime, dateTime);
    }

    public static Item createOne(LocalDateTime locDateTime) {
        ZonedDateTime dateTime = locDateTime.atZone(ZoneId.systemDefault());
        return new Item("1", "IPHONE 10","used phone","5000","user",
                dateTime, dateTime);
    }


    public static Item createOneForToday() {
        return createOneForDate(LocalDate.now());
    }

    public static Item createOneForDate(LocalDate date) {
        return createOne(date.atStartOfDay().atZone(ZoneId.systemDefault()));
    }

}
