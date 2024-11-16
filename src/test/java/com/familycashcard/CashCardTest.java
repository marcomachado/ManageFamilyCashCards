package com.familycashcard;

import com.familycashcard.cashcard.CashCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CashCardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should return OK for endpoint'")
    void t1() throws Exception {
        mockMvc.perform(get("/cashcards"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return a list of cash cards with 2 cards")
    void t2() throws Exception {
        mockMvc.perform(get("/cashcards"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].amount", is(11.2)))
                .andExpect(jsonPath("$[0].owner", is("Mark")))

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].amount", is(22.1)))
                .andExpect(jsonPath("$[1].owner", is("Jimmy")));
    }

    @Test
    @DisplayName("should return cash card with id 1")
    void t3() throws Exception {
        MvcResult result = mockMvc.perform(get("/cashcards/1"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        CashCard cashCard = objectMapper.readValue(json, CashCard.class);

        assertNotNull(cashCard);
        assertEquals(1L, cashCard.getId());
        assertEquals(11.2, cashCard.getAmount());
        assertEquals("Mark", cashCard.getOwner());
    }

    @Test
    @DisplayName("should return not found cash card with id 99")
    void t4() throws Exception {
        mockMvc.perform(get("/cashcards/99"))
                .andExpect(status().isNotFound());
    }
}
