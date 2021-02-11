package com.github.starter.app.item.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;


public class Item implements Serializable {
    private final String id;
    private final String name;
    private final String description;
    private final String price;

    @JsonProperty("action_by")
    private final String actionBy;

    private final ZonedDateTime created;


    private final ZonedDateTime updated;

    @JsonCreator
    public Item(@JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("price") String price,
                @JsonProperty("action_by") String actionBy,
                @JsonProperty("created") ZonedDateTime created,
                @JsonProperty("updated") ZonedDateTime updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.actionBy = actionBy;
        this.created = created;
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getActionBy() {
        return actionBy;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return new StringBuilder("id:").append(id).append(", name: ").append(", description: ").append(description)
                .append(", price: ").append(price).append(", action_by: ").append(actionBy)
            .append(", created: ").append(created).append(", updated: ").append(updated).toString();
    }
}
