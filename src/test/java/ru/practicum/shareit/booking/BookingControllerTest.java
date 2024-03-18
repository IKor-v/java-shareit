package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingServiceImpl bookingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1L, "Link", "spuderman@man.com");
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, null);
    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1L, now.plusMinutes(5).toString(), now.plusHours(1).toString(),
            ItemMapper.toItemDto(item), UserMapper.toUserDto(user), BookingStatus.WAITING);
    private final Booking booking = new Booking(1L, now.plusMinutes(5), now.plusHours(1), item, user, BookingStatus.WAITING);
    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1L, now.plusMinutes(5).toString(), now.plusHours(1).toString());

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(bookingDtoIn, user.getId())).thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd())))
                //.andExpect(jsonPath("$.item", is(bookingDtoOut.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingDtoOut.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void bookingApproved() throws Exception{
        when(bookingService.bookingApproved(user.getId(), item.getId(), true)).thenReturn(bookingDtoOut);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd())))
                //.andExpect(jsonPath("$.item", is(bookingDtoOut.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingDtoOut.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(user.getId(), booking.getId())).thenReturn(bookingDtoOut);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd())))
                //.andExpect(jsonPath("$.item", is(bookingDtoOut.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingDtoOut.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getAllBookingByBooker() throws Exception {
        when(bookingService.getAllBookingByBooker(user.getId(), "ALL", 0, 50)).thenReturn(List.of(bookingDtoOut));

        String result =mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(bookingDtoOut)));
    }

    @Test
    void getAllBookingByOwner() throws Exception {
        when(bookingService.getAllBookingByOwner(user.getId(), "ALL", 0, 50)).thenReturn(List.of(bookingDtoOut));

        String result =mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(result, mapper.writeValueAsString(List.of(bookingDtoOut)));
    }
}