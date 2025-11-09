package com.flightsfetcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsfetcher.model.Coordinates;
import com.flightsfetcher.service.CoordinatesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoordinatesController.class)
class CoordinatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoordinatesService coordinatesService;

    @Test
    void testGetCurrentCoordinates() throws Exception {
        Coordinates coordinates = new Coordinates(40.7128, -74.0060, 50.0);
        when(coordinatesService.getCurrentCoordinates()).thenReturn(coordinates);

        mockMvc.perform(get("/api/coordinates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060))
                .andExpect(jsonPath("$.radius").value(50.0));

        verify(coordinatesService, times(1)).getCurrentCoordinates();
    }

    @Test
    void testUpdateCoordinates() throws Exception {
        Coordinates coordinates = new Coordinates(51.5074, -0.1278, 75.0);

        mockMvc.perform(post("/api/coordinates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordinates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coordinates updated successfully"))
                .andExpect(jsonPath("$.coordinates.latitude").value(51.5074))
                .andExpect(jsonPath("$.coordinates.longitude").value(-0.1278))
                .andExpect(jsonPath("$.coordinates.radius").value(75.0));

        verify(coordinatesService, times(1)).updateCoordinates(any(Coordinates.class));
    }

    @Test
    void testUpdateCoordinatesValidationFailure() throws Exception {
        Coordinates invalidCoordinates = new Coordinates(null, -74.0060, 50.0);

        mockMvc.perform(post("/api/coordinates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCoordinates)))
                .andExpect(status().isBadRequest());

        verify(coordinatesService, never()).updateCoordinates(any(Coordinates.class));
    }
}
