package com.github.starter.app.item.endpoints;


import com.github.starter.app.item.model.Item;
import com.github.starter.app.item.model.SearchRequest;
import com.github.starter.app.item.service.ItemServiceFactory;
import com.github.starter.core.container.Container;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
@CrossOrigin(value = "*")
public class ItemEndpoints {

    private final ItemServiceFactory serviceFactory;

    @Autowired
    ItemEndpoints(ItemServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GetMapping("/{mode}/")
    public Mono<Container<List<Item>>> list(@PathVariable("mode") String mode,
                                            @RequestParam(value = "created", required = false, defaultValue = "") String created,
                                            @RequestParam(value = "action_by", required = false, defaultValue = "")String actionBy,
                                            @RequestParam(value = "price", required = false, defaultValue = "")String price) {
        SearchRequest searchRequest = new SearchRequest(actionBy, price, created);
        return this.serviceFactory.findHandler(mode).listItems(searchRequest).map(Container::new);
    }

    @GetMapping("/{mode}/{id}")
    @Counted(recordFailuresOnly = false, description = "find by id")
    public Mono<Container<Item>> get(@PathVariable("mode") String mode, @PathVariable("id") String id) {
        return serviceFactory.findHandler(mode).findById(id).map(Container::new);
    }

    @PostMapping(value = "/{mode}/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed(description = "save time")
    public Mono<Container<Item>> add(@PathVariable("mode") String mode, @RequestBody Item todoItem) {
        return serviceFactory.findHandler(mode).save(todoItem).map(Container::new);
    }

    @PostMapping(value = "/{mode}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Container<Item>> update(@PathVariable("mode") String mode, @PathVariable("id") String id, @RequestBody Item todoItem) {
        return serviceFactory.findHandler(mode).update(id, todoItem).map(Container::new);
    }

    @DeleteMapping("/{mode}/{id}")
    public Mono<Container<Map<String, Boolean>>> delete(@PathVariable("mode") String mode, @PathVariable("id") String id) {
        return serviceFactory.findHandler(mode).delete(id).map(b -> Map.of("result", b)).map(Container::new);
    }
}

