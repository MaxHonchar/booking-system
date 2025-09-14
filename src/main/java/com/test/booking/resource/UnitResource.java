package com.test.booking.resource;

import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.enums.AccommodationType;
import com.test.booking.enums.EventType;
import com.test.booking.service.ICacheService;
import com.test.booking.service.IUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static com.test.booking.utils.CommonUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/unit")
@RequiredArgsConstructor
@Tag(name = "Unit", description = "manipulate units")
public class UnitResource {

    private final ICacheService cacheService;
    private final IUnitService unitService;

    @Operation(operationId = "searchUnits", description = "search units by conditions",
    parameters = {
            @Parameter(name = "accommodationType", description = "type of accommodation", example = "FLAT", allowEmptyValue = true),
            @Parameter(name = "eventType", description = "type of event", example = "DELETE", allowEmptyValue = true),
            @Parameter(name = "fromDate", description = "from date ", example = "2025-09-01", allowEmptyValue = true),
            @Parameter(name = "toDate", description = "to date ", example = "2025-09-30", allowEmptyValue = true),
            @Parameter(name = "minCost", description = "min cost ", example = "100", allowEmptyValue = true),
            @Parameter(name = "maxCost", description = "max cost ", example = "800", allowEmptyValue = true)
    })
    @GetMapping("/search")
    public Page<UnitDto> findUnitsByFilters(
            @RequestParam(required = false, defaultValue = "") String accommodationType,
            @RequestParam(required = false, defaultValue = "") String eventType,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost,
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "cost", direction = Sort.Direction.ASC)
            })
            Pageable pageable) {
        UnitSearchDto searchDto = getUnitSearchDto(accommodationType, eventType, fromDate, toDate, minCost, maxCost);
        return unitService.getAvailableUnits(searchDto, pageable);
    }

    private UnitSearchDto getUnitSearchDto(String accommodationType,
                                           String event,
                                           LocalDate fromDate,
                                           LocalDate toDate,
                                           Double minCost,
                                           Double maxCost) {
        AccommodationType type = getAccommodationType(accommodationType);
        EventType eventType = getEventType(event);
        Instant start = convertLocalDateToInstant(fromDate).orElse(null);
        Instant end = convertLocalDateToInstant(toDate).orElse(null);
        BigDecimal minDecimal = getDecimal(minCost);
        BigDecimal maxDecimal = getDecimal(maxCost);
        return UnitSearchDto.builder()
                .eventType(eventType)
                .accommodationType(type)
                .fromDate(start)
                .toDate(end)
                .minCost(minDecimal)
                .maxCost(maxDecimal)
                .build();
    }

    @Operation(description = "Create unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = UnitDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create unit",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UnitDto.class))}),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<UnitDto> createUnit(@RequestBody UnitDto unitDto) {
        Optional<UnitDto> optional = unitService.createUnit(unitDto);
        optional.ifPresent(tmp -> cacheService.updateTotalUnits());
        return optional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @Operation(description = "Get total amount of units")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get amount"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalUnits() {
        return Optional.ofNullable(cacheService.getTotalUnits())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
