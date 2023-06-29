package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemRequestDto itemRequestDto, long userId) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getItem(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(long userId, ItemRequestDto itemRequestDto, long itemId) {
        return patch("/" + itemId, userId, itemRequestDto);
    }

    public void deleteItem(long userId, long itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "text", text
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }
}
