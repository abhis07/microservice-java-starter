package com.github.starter.app.item.service;

import com.github.starter.app.item.model.Item;
import com.github.starter.app.item.model.SearchRequest;
import com.github.starter.app.item.repository.ItemRepository;
import com.github.starter.core.exception.BadRequest;
import com.github.starter.grpc.model.TodoItems;
import com.github.starter.proto.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service("defaultTodoService")
@Primary
public class DefaultItemService implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    DefaultItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Mono<List<Item>> listItems(SearchRequest searchRequest) {
        return itemRepository.listItems(
            Items.SearchRequest.newBuilder()
                .setCreated(searchRequest.getCreated())
                .setPrice(searchRequest.getPrice())
                .setActionBy(searchRequest.getActionBy())
                .build()
        ).map(tasks -> tasks.stream().map(TodoItems::itemItems).collect(Collectors.toList()));
    }


    @Override
    public Mono<Item> findById(String id) {
        return itemRepository.findById(id).map(TodoItems::itemItems);
    }

    @Override
    public Mono<Item> save(Item item) {
        return itemRepository.add(TodoItems.items(item)).map(TodoItems::itemItems);
    }

    @Override
    public Mono<Item> update(String id, Item item) {
        if (!id.equals(item.getId())) {
            return Mono.error(BadRequest.forCodeAndMessage("invalid-id", "Provided Item ID does not match one in Payload"));
        }
        return itemRepository.update(TodoItems.items(item)).map(TodoItems::itemItems);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return itemRepository.delete(id);
    }

    @Override
    public String getName() {
        return "default";
    }
}
