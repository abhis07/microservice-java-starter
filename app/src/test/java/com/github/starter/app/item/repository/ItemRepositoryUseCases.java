package com.github.starter.app.item.repository;

import com.github.starter.app.item.endpoints.Items;
import com.github.starter.app.item.model.Item;
import com.github.starter.app.item.repository.ItemRepository;
import com.github.starter.grpc.model.TodoItems;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public class ItemRepositoryUseCases {

    private final ItemRepository itemRepository;

    ItemRepositoryUseCases(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public void testListItemTasks() {
        List<com.github.starter.proto.Items.Item> tasks = itemRepository.listItems(
            com.github.starter.proto.Items.SearchRequest.newBuilder()
                .setActionBy("")
                .build()).block();
        Assertions.assertFalse(tasks.isEmpty(), "todo table should have some data");
    }

    public void verifyAddItemTask() {
        Item task = Items.createOne(LocalDateTime.now());
        com.github.starter.proto.Items.Item savedTask = itemRepository.add(TodoItems.items(task)).block();
        Assertions.assertEquals(task.getDescription(), savedTask.getDescription());

        String taskId = savedTask.getId();
        com.github.starter.proto.Items.Item storedTask = itemRepository.findById(taskId).block();
        Assertions.assertEquals(savedTask.getDescription(), storedTask.getDescription());
    }

    public void verifyDeleteTodoTask() {
        Item task = Items.createOne(LocalDateTime.now());
        com.github.starter.proto.Items.Item savedTask = itemRepository.add(TodoItems.items(task)).block();
        Assertions.assertEquals(task.getDescription(), savedTask.getDescription());

        String taskId = savedTask.getId();
        Assertions.assertTrue(itemRepository.delete(taskId).block());

        Assertions.assertFalse(itemRepository.delete(taskId).onErrorReturn(false).block());
    }

    public void verifyUpdateTodoTask() {
        Item task = Items.createOne(LocalDateTime.now());
        com.github.starter.proto.Items.Item savedTask = itemRepository.add(TodoItems.items(task)).block();
        Assertions.assertEquals(task.getDescription(), savedTask.getDescription());

        String taskId = savedTask.getId();
        itemRepository.update(
            TodoItems.items(new Item(taskId,"IPHONE 11", savedTask.getDescription(), "5000","user1", ZonedDateTime.now(),
                ZonedDateTime.now()))
        ).block();

        com.github.starter.proto.Items.Item updatedTask = itemRepository.findById(taskId).block();
        Assertions.assertEquals("5000", updatedTask.getPrice());
    }
}
