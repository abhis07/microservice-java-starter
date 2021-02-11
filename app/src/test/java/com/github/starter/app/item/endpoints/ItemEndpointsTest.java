package com.github.starter.app.item.endpoints;

import com.github.starter.app.item.endpoints.ItemEndpoints;
import com.github.starter.app.item.model.Item;
import com.github.starter.app.item.model.SearchRequest;
import com.github.starter.app.item.service.ItemService;
import com.github.starter.app.item.service.ItemServiceFactory;
import com.github.starter.core.advice.CustomErrorAttributes;
import com.github.starter.core.advice.GlobalErrorHandler;
import com.github.starter.core.consumer.MonoConsumer;
import com.github.starter.core.exception.InternalServerError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@DisplayName("Item Endpoints")
@ExtendWith(SpringExtension.class)
public class ItemEndpointsTest {
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext applicationContext;

    @ParameterizedTest(name = "Error Scenario - [{index}] {0} - {4} {1}")
    @MethodSource("data")
    public void testItemsErrorService(String scenarioName, String uri, Consumer<ItemService> serviceHook, Class clz, HttpMethod method) {
        verifyInternalServiceErrorResponse(uri, serviceHook::accept, clz, method);
    }

    private <R> void verifyInternalServiceErrorResponse(String uri, Consumer<ItemService> serviceHook, Class<R> clz, HttpMethod method) {
        ItemService itemService = Mockito.mock(ItemService.class);
        serviceHook.accept(itemService);
        ItemServiceFactory serviceFactory = new ItemServiceFactory(List.of(itemService));
        ItemEndpoints item = new ItemEndpoints(serviceFactory);
        CustomErrorAttributes errorAttributes = new CustomErrorAttributes();
        GlobalErrorHandler globalErrorHandler = new GlobalErrorHandler(errorAttributes, applicationContext, new DefaultServerCodecConfigurer());
        WebTestClient webTestClient = WebTestClient.bindToController(item, globalErrorHandler).build();

        Mono<R> result = Mono.from(webTestClient.method(method).uri(uri).exchange().expectStatus().is5xxServerError().returnResult(clz).getResponseBody());
        new MonoConsumer<>(result, false).drain();
        StepVerifier.create(result).expectComplete().verify();
    }

    private static Stream<Arguments> data() {
        Item todoItem = Items.createOneForToday();
        String id = todoItem.getId();
        SearchRequest searchRequest = new SearchRequest("", "", "");
        return Stream.of(
            Arguments.of(
                "Find Items test", "/item/123",
                (Consumer<ItemService>) itemService -> Mockito.when(itemService.findById("123")).thenReturn(Mono.error(InternalServerError::new)), Map.class, HttpMethod.GET
            ),
            Arguments.of(
                "Delete Items id", "/item/123",
                (Consumer<ItemService>) itemService -> Mockito.when(itemService.delete("123")).thenReturn(Mono.error(InternalServerError::new)), Map.class, HttpMethod.DELETE
            ),
            Arguments.of(
                "Update Todos", String.format("/items/%s", id),
                (Consumer<ItemService>) itemService -> Mockito.when(itemService.update(id, todoItem)).thenReturn(Mono.error(InternalServerError::new)), Map.class, HttpMethod.POST
            ),
            Arguments.of(
                "Add Todos", "/items/",
                (Consumer<ItemService>) itemService -> Mockito.when(itemService.save(todoItem)).thenReturn(Mono.error(InternalServerError::new)), Map.class, HttpMethod.POST
            ),
            Arguments.of(
                "Search Todos", "/items/search",
                (Consumer<ItemService>) itemService -> Mockito.when(itemService.listItems(searchRequest)).thenReturn(Mono.error(InternalServerError::new)), Map.class, HttpMethod.GET
            )
        );
    }

}
