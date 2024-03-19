package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final User user = new User(1L, "Link", "spuderman@man.com");
    @Autowired
    ObjectMapper mapper;
    LocalDateTime now = LocalDateTime.now();
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, now);
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, itemRequest);
    private final ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(1L, "description",
            UserMapper.toUserDto(user), now, List.of(ItemMapper.toItemDtoIn(item)));
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", UserMapper.toUserDto(user), now);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestServiceImpl requestService;

    @Test
    void addRequestTest() throws Exception {
        when(requestService.addRequest(user.getId(), itemRequestDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        //.andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor())))
        //.andExpect(jsonPath("$.created", is (itemRequestDto.getCreated().toString())));

    }

    @Test
    void getRequestsFromUserTest() throws Exception {
        when(requestService.getRequestsFromUser(user.getId())).thenReturn(List.of(itemRequestDtoOut));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(itemRequestDtoOut)));
    }

    @Test
    void getRequestsFromOtherUserTest() throws Exception {
        when(requestService.getRequestsFromOtherUser(user.getId(), 0, 50)).thenReturn(List.of(itemRequestDtoOut));

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(itemRequestDtoOut)));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(user.getId(), itemRequest.getId())).thenReturn(itemRequestDtoOut);

        String result = mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(result, mapper.writeValueAsString(itemRequestDtoOut));
    }
}