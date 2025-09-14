package com.test.booking.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UserDto;
import com.test.booking.resource.BookingResource;
import com.test.booking.service.IBookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingResource.class)
class BookingResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IBookingService bookingService;

    @Test
    @DisplayName("POST /api/booking - should create booking and return 200")
    void createBooking_success() throws Exception {
        CreateBookingDto createDto = CreateBookingDto.builder()
                .unitId(1L)
                .userEmail("john@example.com")
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(3))
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(10L)
                .unit(UnitDto.builder().id(1L).description("Nice flat").build())
                .user(UserDto.builder().email("john@example.com").build())
                .checkIn(Instant.now())
                .checkOut(Instant.now().plusSeconds(86400))
                .build();

        Mockito.when(bookingService.save(any(CreateBookingDto.class)))
                .thenReturn(Optional.of(bookingDto));

        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/booking - should return 204 if no booking created")
    void createBooking_noContent() throws Exception {
        Mockito.when(bookingService.save(any(CreateBookingDto.class)))
                .thenReturn(Optional.empty());

        CreateBookingDto createDto = CreateBookingDto.builder().unitId(1L).build();

        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/booking/pay/{id} - should pay booking and return 200")
    void payBooking_success() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(11L)
                .user(UserDto.builder().email("pay@example.com").build())
                .build();

        Mockito.when(bookingService.pay(eq(11L), eq(100.0)))
                .thenReturn(Optional.of(bookingDto));

        mockMvc.perform(patch("/api/booking/pay/{id}", 11L)
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11L))
                .andExpect(jsonPath("$.user.email").value("pay@example.com"));
    }

    @Test
    @DisplayName("PATCH /api/booking/pay/{id} - should return 204 if payment fails")
    void payBooking_noContent() throws Exception {
        Mockito.when(bookingService.pay(eq(99L), eq(200.0)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/booking/pay/{id}", 99L)
                        .param("amount", "200"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/booking/cancel/{id} - should cancel booking and return 200")
    void cancelBooking_success() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(15L)
                .user(UserDto.builder().email("cancel@example.com").build())
                .build();

        Mockito.when(bookingService.cancel(15L))
                .thenReturn(Optional.of(bookingDto));

        mockMvc.perform(patch("/api/booking/cancel/{id}", 15L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15L))
                .andExpect(jsonPath("$.user.email").value("cancel@example.com"));
    }

    @Test
    @DisplayName("PATCH /api/booking/cancel/{id} - should return 204 if cancel fails")
    void cancelBooking_noContent() throws Exception {
        Mockito.when(bookingService.cancel(77L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/booking/cancel/{id}", 77L))
                .andExpect(status().isNoContent());
    }
}
