package com.onelineaday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.service.JournalService;
import com.onelineaday.service.UserService;
import com.onelineaday.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JournalController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simplicity in this unit test example
class JournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JournalService journalService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.onelineaday.service.StatsService statsService;

    @MockBean
    private com.onelineaday.service.SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void updateEntry_ValidRequest_ReturnsOk() throws Exception {
        JournalController.EntryRequest request = new JournalController.EntryRequest();
        request.setDate(LocalDate.now());
        request.setText("Valid text");

        JournalEntry entry = JournalEntry.builder()
                .id(UUID.randomUUID())
                .date(request.getDate())
                .text(request.getText())
                .build();

        when(journalService.saveOrUpdateEntry(any(), any(), any())).thenReturn(entry);

        mockMvc.perform(put("/api/v1/journal/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Valid text"));
    }

    @Test
    void updateEntry_InvalidRequest_ReturnsBadRequest() throws Exception {
        JournalController.EntryRequest request = new JournalController.EntryRequest();
        request.setDate(null); // Missing date
        request.setText(""); // Empty text

        mockMvc.perform(put("/api/v1/journal/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
