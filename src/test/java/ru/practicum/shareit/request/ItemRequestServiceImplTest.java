package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private final User user = new User(1L, "Link", "spuderman@man.com");
    LocalDateTime now = LocalDateTime.now();
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, now);
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, itemRequest);
    private final ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(1L, "description",
            UserMapper.toUserDto(user), now, List.of(ItemMapper.toItemDtoIn(item)));
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", UserMapper.toUserDto(user), now);
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    void addRequestTest() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto result = requestService.addRequest(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getRequestor());
        assertEquals(itemRequestDto.getId(), result.getId());
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsFromUserTest() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestNotNull()).thenReturn(List.of(item));
        List<ItemRequestDtoOut> result = requestService.getRequestsFromUser(user.getId());

        assertEquals(List.of(itemRequestDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByIdOrderByCreatedDesc(anyLong());
        verify(itemRepository, times(1)).findAllByRequestNotNull();
    }

    @Test
    void getRequestsFromOtherUserTest() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByIdNotOrderByCreatedDesc(anyLong(), any())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestNotNull()).thenReturn(List.of(item));
        List<ItemRequestDtoOut> result = requestService.getRequestsFromOtherUser(user.getId(), 0, 50);

        assertEquals(List.of(itemRequestDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByIdNotOrderByCreatedDesc(anyLong(), any());
        verify(itemRepository, times(1)).findAllByRequestNotNull();
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDtoOut result = requestService.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDtoOut, result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByRequestId(anyLong());
    }
}