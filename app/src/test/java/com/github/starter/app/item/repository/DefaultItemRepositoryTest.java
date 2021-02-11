package com.github.starter.app.item.repository;

import com.github.starter.Application;
import com.github.starter.app.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = {Application.class})
public class DefaultItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    private ItemRepositoryUseCases itemRepositoryUseCases;

    @BeforeEach
    public void setup() {
        itemRepositoryUseCases = new ItemRepositoryUseCases(itemRepository);
    }

    @DisplayName("list item")
    @Test
    public void testListItem() {
        itemRepositoryUseCases.testListItemTasks();
    }

    @DisplayName("add item")
    @Test
    public void testAddItemTask() {
        itemRepositoryUseCases.verifyAddItemTask();
    }


    @DisplayName("delete item")
    @Test
    public void testDeleteItemTask() {
        itemRepositoryUseCases.verifyDeleteTodoTask();
    }


    @DisplayName("update item")
    @Test
    public void testUpdateTodoTask() {
        itemRepositoryUseCases.verifyUpdateTodoTask();
    }

}
