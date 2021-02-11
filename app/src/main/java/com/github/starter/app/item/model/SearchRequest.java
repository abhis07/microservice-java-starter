package com.github.starter.app.item.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SearchRequest {
    private String actionBy;
    private String price;
    private String created;

    @JsonCreator
    public SearchRequest(String actionBy, String price, String created) {
        this.actionBy = actionBy;
        this.price = price;
        this.created = created;
    }

    public String getActionBy() {
        return actionBy;
    }

    public String getPrice() {
        return price;
    }

    public String getCreated() {
        return created;
    }
}
