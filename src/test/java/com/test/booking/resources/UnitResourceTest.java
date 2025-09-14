package com.test.booking.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitPropertiesDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.resource.UnitResource;
import com.test.booking.service.ICacheService;
import com.test.booking.service.IUnitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnitResource.class)
public class UnitResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private IUnitService unitService;

    @MockitoBean
    private ICacheService cacheService;

    @Nested
    @DisplayName("GET /api/unit/search")
    public class SearchUnits {

        @ParameterizedTest
        @CsvSource({
                "FLAT,CREATE,100.0,500.0",
                "HOME,,200.0,800.0",
                ",DELETE,,",
                "APARTMENTS,CREATE,50.0,150.0"
        })
        @DisplayName("should return units for different filters")
        void testSearchUnits(String accommodationType,
                             String eventType,
                             String minCost,
                             String maxCost) throws Exception {

            Pageable pageable = PageRequest.of(0, 20, Sort.by("cost").ascending());
            UnitDto dto = UnitDto.builder()
                    .id(1L)
                    .cost(250.0)
                    .description("Nice flat")
                    .properties(Set.of(UnitPropertiesDto.builder()
                            .id(1L)
                            .type("FLAT")
                            .rooms(2)
                            .floor(3)
                            .build()))
                    .build();
            Page<UnitDto> page = new PageImpl<>(List.of(dto), pageable, 1);

            when(unitService.getAvailableUnits(any(UnitSearchDto.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/unit/search")
                            .param("accommodationType", accommodationType == null ? "" : accommodationType)
                            .param("eventType", eventType == null ? "" : eventType)
                            .param("minCost", minCost == null ? "" : minCost)
                            .param("maxCost", maxCost == null ? "" : maxCost)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].description").value("Nice flat"));

            ArgumentCaptor<UnitSearchDto> captor = ArgumentCaptor.forClass(UnitSearchDto.class);
            verify(unitService).getAvailableUnits(captor.capture(), any(Pageable.class));

            UnitSearchDto captured = captor.getValue();
            if (minCost != null && !minCost.isBlank()) {
                assertThat(captured.getMinCost()).isEqualTo(new BigDecimal(minCost));
            }
            if (maxCost != null && !maxCost.isBlank()) {
                assertThat(captured.getMaxCost()).isEqualTo(new BigDecimal(maxCost));
            }
        }
    }

    @Nested
    @DisplayName("POST /api/unit")
    public class CreateUnit {

        @Test
        @DisplayName("should create unit and update cache")
        void testCreateUnitSuccess() throws Exception {
            UnitDto dto = UnitDto.builder()
                    .id(10L)
                    .cost(500.0)
                    .description("Luxury Home")
                    .build();

            when(unitService.createUnit(any(UnitDto.class))).thenReturn(Optional.of(dto));

            mockMvc.perform(post("/api/unit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.description").value("Luxury Home"));

            verify(cacheService).updateTotalUnits();
        }

        @Test
        @DisplayName("should return 204 when unit creation fails")
        void testCreateUnitNoContent() throws Exception {
            UnitDto dto = UnitDto.builder().description("Invalid").build();
            when(unitService.createUnit(any(UnitDto.class))).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/unit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isNoContent());

            verify(cacheService, never()).updateTotalUnits();
        }
    }

    @Nested
    @DisplayName("GET /api/unit/total")
    class GetTotalUnits {

        @Test
        @DisplayName("should return total units from cache")
        void testGetTotalUnitsSuccess() throws Exception {
            when(cacheService.getTotalUnits()).thenReturn(42L);

            mockMvc.perform(get("/api/unit/total"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("42"));
        }

        @Test
        @DisplayName("should return 404 when no value in cache")
        void testGetTotalUnitsNotFound() throws Exception {
            when(cacheService.getTotalUnits()).thenReturn(null);

            mockMvc.perform(get("/api/unit/total"))
                    .andExpect(status().isNotFound());
        }
    }
}
