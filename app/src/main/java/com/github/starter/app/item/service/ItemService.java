package com.github.starter.app.item.service;

import com.github.starter.app.item.model.Item;
import com.github.starter.app.item.model.SearchRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ItemService {

    Mono<List<Item>> listItems(SearchRequest searchRequest);


    Mono<Item> findById(String id);

    Mono<Item> save(Item item);

    Mono<Item> update(String id, Item item);

    Mono<Boolean> delete(String id);

    String getName();
}
