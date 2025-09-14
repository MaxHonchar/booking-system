package com.test.booking.resource;

import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.service.IBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "manipulate bookings")
public class BookingResource {

    private final IBookingService bookingService;

    @Operation(description = "Create booking",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = CreateBookingDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create booking",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingDto dto) {
        return bookingService.create(dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @Operation(description = "Simulation of payment", parameters = {
            @Parameter(name = "id", required = true, description = "booking id"),
            @Parameter(name = "amount", required = true, description = "required amount for booking")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking payed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/pay/{id}")
    public ResponseEntity<BookingDto> payForBooking(@PathVariable Long id,
                                                    @RequestParam Double amount) {
        return bookingService.pay(id, amount)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(description = "Canceled booking", parameters = {
            @Parameter(name = "id", required = true, description = "booking id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking canceled",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id) {
        return bookingService.cancel(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }



}
