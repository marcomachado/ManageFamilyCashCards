package com.familycashcard;

import com.familycashcard.cashcard.CashCard;
import com.familycashcard.cashcard.CashCardDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("should return a list of cash cards with 22 cards")
    void t2() throws Exception {
        mockMvc.perform(get("/cashcards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(22)));
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

    @Test
    @DisplayName("should return 3 cash cards belonging to 'Clara'")
    void t5() throws Exception {
        MvcResult result = mockMvc.perform(get("/cashcards/getcards/Clara"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<CashCard> cards = objectMapper.readValue(json, new TypeReference<>(){});

        assertNotNull(cards);
        assertEquals(3, cards.size());
    }

    @Test
    @DisplayName("should create a new cash card with ID=23")
    void t6() throws Exception {
        var cashCard = new CashCardDTO(121D, "Marco");

        mockMvc.perform(post("/cashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cashCard))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/cashcards/23")));
    }

    @Test
    @DisplayName("should return cash card with id 23")
    void t7() throws Exception {
        MvcResult result = mockMvc.perform(get("/cashcards/23"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        CashCard cashCard = objectMapper.readValue(json, CashCard.class);

        assertNotNull(cashCard);
        assertEquals(23L, cashCard.getId());
        assertEquals(121, cashCard.getAmount());
        assertEquals("Marco", cashCard.getOwner());
    }

    @Test
    @DisplayName("should update cash card with id 23")
    void t8() throws Exception {
        var oldCashCard = new CashCardDTO(121D, "Marco");
        var newCashCard = new CashCardDTO(23.23, "Mark Zuck");

        mockMvc.perform(put("/cashcards/23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCashCard))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/cashcards/23"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        CashCard cashCard = objectMapper.readValue(json, CashCard.class);

        assertNotNull(cashCard);
        assertEquals(23, cashCard.getId());
        assertEquals(23.23, cashCard.getAmount());
        assertEquals("Mark Zuck", cashCard.getOwner());

        assertNotEquals(oldCashCard.getAmount(), cashCard.getAmount());
        assertNotEquals(oldCashCard.getOwner(), cashCard.getOwner());
    }

    @Test
    @DisplayName("should not update cash card with id 99 - not exists - and return not found error")
    void t9() throws Exception {
        var newCashCard = new CashCardDTO(23.23, "Mark Zuck");

        mockMvc.perform(put("/cashcards/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCashCard))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete cash card with id 23")
    void t10() throws Exception {

        mockMvc.perform(delete("/cashcards/23"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cashcards/23"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not delete cash card with id 99 - not exists - and return not found")
    void t11() throws Exception {
        mockMvc.perform(delete("/cashcards/99"))
                .andExpect(status().isNotFound());
    }
}
