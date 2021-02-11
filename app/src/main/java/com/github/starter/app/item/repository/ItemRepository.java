package com.github.starter.app.item.repository;

import com.github.starter.proto.Items;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ItemRepository {
    Mono<List<Items.Item>> listItems(Items.SearchRequest searchRequest);

    Mono<Items.Item> findById(String id);

    Mono<Items.Item> add(Items.Item items);

    Mono<Items.Item> update(Items.Item items);

    Mono<Boolean> delete(String id);
}
