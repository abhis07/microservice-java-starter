package com.github.starter.grpc.model;

import com.github.starter.app.item.model.Item;
import com.github.starter.proto.Items;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TodoItems {

    public static Item itemItems(com.github.starter.proto.Items.Item item) {
        ZonedDateTime dateTime = ZonedDateTime.parse(item.getCreated(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return new Item(item.getId(),item.getName(), item.getDescription(),item.getPrice(), item.getActionBy(), dateTime, dateTime);
    }

    public static com.github.starter.proto.Items.Item items(Item item) {
        String id = item.getId();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        String created = item.getCreated().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return com.github.starter.proto.Items.Item.newBuilder()
            .setId(id).setName(item.getName()).setPrice(item.getPrice())
            .setActionBy(item.getActionBy())
            .setCreated(created)
            .setDescription(item.getDescription())
            .setUpdated(item.getUpdated() != null ? item.getUpdated().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : created)
            .build();
    }

}
