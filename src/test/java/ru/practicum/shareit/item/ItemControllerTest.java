package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImp itemService;

    private final User user = new User(1L, "Link", "spuderman@man.com");
    private final ItemDto itemDto = new ItemDto(1L, "Ocarina", "This is time thing", true,
            UserMapper.toUserDto(user), null, null, null, List.of());
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, null);
    private final ItemDtoIn itemDtoIn = new ItemDtoIn(1L, "Ocarina", "This is time thing", true, null);
    private final CommentDtoIn commentDtoIn = new CommentDtoIn(1L, user.getName(), "This is comment", LocalDateTime.now().toString());

    @Test
    void addItemTest() throws Exception  {
        when(itemService.addItem(user.getId(), itemDtoIn)).thenReturn(itemDtoIn);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                    .content(mapper.writeValueAsString(itemDtoIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoIn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoIn.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoIn.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoIn.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoIn.getRequestId())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(user.getId(), item.getId(), itemDtoIn)).thenReturn(itemDtoIn);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoIn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoIn.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoIn.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoIn.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoIn.getRequestId())));
    }

    @Test
    void getItemForIdTest() throws Exception {
        when(itemService.getItemForId(user.getId(), item.getId())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemDto.getRequest())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())));
                //.andExpect(jsonPath("$.owner", is(itemDto.getOwner())));
    }

    @Test
    void getAllMyItemTest() throws Exception {
        when(itemService.getAllMyItem(user.getId(), 0, 50)).thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(itemDto)));
    }

    @Test
    void searchForTextTest() throws Exception {
        when(itemService.searchForText("This text.", 0, 50)).thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "This text.")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(itemDto)));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(user.getId(), item.getId(), commentDtoIn)).thenReturn(commentDtoIn);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoIn.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoIn.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDtoIn.getText())))
                .andExpect(jsonPath("$.created", is(commentDtoIn.getCreated())));

    }
}