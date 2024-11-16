package com.familycashcard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CashCardTest {

    @Autowired
    private MockMvc mockMvc;

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

                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].amount", is(11.2)))
                .andExpect(jsonPath("$[0].owner", is("Mark")))

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].amount", is(22.1)))
                .andExpect(jsonPath("$[1].owner", is("Jimmy")));
    }
}
