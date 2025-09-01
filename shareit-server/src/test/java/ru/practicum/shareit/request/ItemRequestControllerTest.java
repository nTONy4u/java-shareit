package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_ValidRequest_ShouldReturnCreatedRequest() throws Exception {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill");
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setCreated(LocalDateTime.now());

        when(itemRequestService.createRequest(
                ArgumentMatchers.any(ItemRequestCreateDto.class),
                ArgumentMatchers.eq(1L))
        ).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a drill")));
    }

    @Test
    void getUserRequests_UserWithRequests_ShouldReturnRequests() throws Exception {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Request 1");
        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Request 2");
        List<ItemRequest> requests = Arrays.asList(request1, request2);

        when(itemRequestService.getUserRequests(ArgumentMatchers.eq(1L))).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getAllRequests_WithPagination_ShouldReturnRequests() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Other user request");
        List<ItemRequest> requests = Arrays.asList(request);

        when(itemRequestService.getAllRequests(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(0),
                ArgumentMatchers.eq(10))
        ).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getRequestById_ExistingRequest_ShouldReturnRequest() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Specific request");

        when(itemRequestService.getRequestById(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(1L))
        ).thenReturn(request);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Specific request")));
    }

    @Test
    void getRequestById_NonExistingRequest_ShouldReturnNotFound() throws Exception {
        when(itemRequestService.getRequestById(
                ArgumentMatchers.eq(999L),
                ArgumentMatchers.eq(1L))
        ).thenThrow(new ItemRequestNotFoundException("Request not found"));

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}