package com.github.starter.app.item.repository;

import com.github.starter.app.config.JdbcClientFactory;
import com.github.starter.core.exception.InternalServerError;
import com.github.starter.core.utils.DateUtils;
import com.github.starter.proto.Items;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Repository
public class DefaultItemRepository implements ItemRepository {

    private final DatabaseClient databaseClient;

    @Autowired
    public DefaultItemRepository(JdbcClientFactory clientFactory, @Value("${flags.default-jdbc-client}") String jdbcClientName) {
        this.databaseClient = clientFactory.forName(jdbcClientName).client();
    }

    @Override
    public Mono<List<Items.Item>> listItems(Items.SearchRequest searchRequest) {
        DatabaseClient.GenericSelectSpec select = databaseClient.select()
                .from("todo.item").project("*");

        if (searchRequest.getActionBy() != "") {
            select = select.matching(Criteria.where("action_by").is(searchRequest.getActionBy()));
        }
        if (searchRequest.getPrice() != "") {
            select = select.matching(Criteria.where("price").is(searchRequest.getPrice()));
        }
        return select.map(this::asItem).all().collectList();
    }

    private Items.Item asItem(Row row) {
        return Items.Item.newBuilder()
                .setId(row.get("id", String.class))
                .setId(row.get("name", String.class))
                .setId(row.get("price", String.class))
                .setActionBy(row.get("action_by", String.class))
                .setCreated(DateUtils.safeDateSerialise(row.get("created")))
                .setUpdated(DateUtils.safeDateSerialise(row.get("updated")))
                .setDescription(row.get("description", String.class))
                .build();
    }


    @Override
    public Mono<Items.Item> findById(String id) {
        return databaseClient
                .execute("select * from todo.items where id= $1").bind("$1", id)
                .map(this::asItem)
                .first();
    }

    @Override
    public Mono<Items.Item> add(Items.Item items) {
        String id = UUID.randomUUID().toString();
        ZonedDateTime updated = ZonedDateTime.now();

        return databaseClient
                .execute("insert into todo.item(id,name, description,price, action_by, created, updated) values($1, $2, $3, $4, $5, $6,$7)")
                .bind("$1", id)
                .bind("$1", items.getName())
                .bind("$2", items.getDescription())
                .bind("$3", items.getActionBy())
                .bind("$4", ZonedDateTime.parse(items.getCreated(), DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .bind("$5", items.getPrice())
                .bind("$6", updated)
                .fetch().rowsUpdated()
                .filter(i -> i == 1)
                .switchIfEmpty(Mono.error(InternalServerError.fromCodeAndMessage("add-error", "Could not add Item record")))
                .then(Mono.just(Items.Item.newBuilder().setId(id).setDescription(items.getDescription())
                        .setActionBy(items.getActionBy())
                        .setCreated(items.getCreated())
                        .setPrice(items.getPrice())
                        .setName(items.getName())
                        .setUpdated(updated.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).build()));
    }

    @Override
    public Mono<Items.Item> update(Items.Item items) {
        ZonedDateTime updatedTime = ZonedDateTime.now();
        return databaseClient.execute("update todo.items set name=$1,price=$2,description=$3, action_by=$4, created=$5, updated=$6 where id=$7")
                .bind("$1", items.getName())
                .bind("$2", items.getPrice())
                .bind("$3", items.getDescription())
                .bind("$4", items.getActionBy())
                .bind("$5", ZonedDateTime.parse(items.getCreated(), DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .bind("$6", updatedTime)
                .bind("$7", items.getId()).fetch().rowsUpdated()
                .filter(i -> i == 1)
                .switchIfEmpty(Mono.error(InternalServerError.fromCodeAndMessage("update-error", "Could not update ITEM record")))
                .then(Mono.just(
                        Items.Item.newBuilder().setId(items.getId()).setDescription(items.getDescription())
                                .setName(items.getPrice())
                                .setPrice(items.getPrice())
                                .setActionBy(items.getActionBy())
                                .setCreated(items.getCreated())
                                .setUpdated(updatedTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).build()));
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return databaseClient.delete().from("todo.item").matching(Criteria.where("id").is(id)).fetch().rowsUpdated()
                .filter(i -> i == 1)
                .switchIfEmpty(Mono.error(InternalServerError.fromCodeAndMessage("delete-error", String.format("Could not delete Item record %s", id))))
                .then(Mono.just(true));
    }
}
