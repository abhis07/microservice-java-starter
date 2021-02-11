package com.github.starter.app.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceFactory {
    private final Map<String, ItemService> itemServices;
    private final ItemService defaultService;

    @Autowired
    public ItemServiceFactory(List<ItemService> itemServices) {
        this.itemServices = itemServices.stream()
                .collect(Collectors.toMap(km -> km.getName(), vm -> vm));
        this.defaultService = this.itemServices.get("default");
    }

    public ItemService findHandler(String intent) {
        return this.itemServices.getOrDefault(intent, this.defaultService);
    }


}
