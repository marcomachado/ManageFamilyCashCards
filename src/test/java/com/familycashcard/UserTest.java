package com.familycashcard;

import com.familycashcard.cashcard.CashCard;
import com.familycashcard.cashcard.CashCardDTO;
import com.familycashcard.user.User;
import com.familycashcard.user.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should return OK for endpoint users'")
    void t1() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return a list of users with size = 10")
    void t2() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("should return user with id 1")
    void t3() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        User recoveredUser = objectMapper.readValue(json, User.class);

        assertNotNull(recoveredUser);
        assertEquals(1L, recoveredUser.getId());
        assertEquals("Alice Silva", recoveredUser.getName());
        assertEquals("alice.silva@example.com", recoveredUser.getEmail());
        assertEquals("alice.silva", recoveredUser.getUsername());
        assertEquals("password123", recoveredUser.getPassword());
        assertTrue(recoveredUser.isActive());
    }

    @Test
    @DisplayName("should return not found user with id 99")
    void t4() throws Exception {
        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 2 users with name 'joão'")
    void t5() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/getusers/joão"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<CashCard> contentList = objectMapper.readValue(json, new TypeReference<>(){});

        assertEquals(2, contentList.size());
    }

    @Test
    @DisplayName("should create a user card with ID=11")
    void t6() throws Exception {
        var newUserDTO = new UserDTO("name", "email@email.com", "username", "password");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/users/11")));

        MvcResult result = mockMvc.perform(get("/users/11"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        User userFromDB = objectMapper.readValue(json, User.class);

        User newUser = newUserDTO.convertToUser();
        newUser.setId(11L);
        newUser.setActive(false);

        assertNotNull(userFromDB);
        assertEquals(newUser, userFromDB);
    }

    @Test
    @DisplayName("should update user with id 11")
    void t7() throws Exception {
        var newUserDTO = new UserDTO("name name", "email_name@email.com",
                "username.user", "password.pass", true);

        mockMvc.perform(put("/users/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/users/11"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        User userFromBD = objectMapper.readValue(json, User.class);

        User newUser = newUserDTO.convertToUser();
        newUser.setId(11L);

        assertNotNull(userFromBD);
        assertEquals(newUser, userFromBD);
    }

    @Test
    @DisplayName("should not update user with id 99 - not exists - and return not found error")
    void t8() throws Exception {
        var newUserDTO = new UserDTO("name name", "email_name@email.com",
                "username.user", "password.pass", true);

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/11"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete cash card with id 23")
    void t10() throws Exception {

        mockMvc.perform(delete("/users/23"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/23"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not delete cash card with id 99 - not exists - and return not found")
    void t11() throws Exception {
        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 3 cash cards belonging to 'Clara; result ordered by default=ID'")
    void t12() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/getcards/Clara"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        DocumentContext documentContext = JsonPath.parse(json);
        String contentJson = documentContext.read("$.content").toString();
        List<CashCard> contentList = objectMapper.readValue(contentJson, new TypeReference<>(){});

        assertEquals(5, contentList.get(0).getId());
        assertEquals(14, contentList.get(1).getId());
        assertEquals(22, contentList.get(2).getId());
    }

    @Test
    @DisplayName("should return a page with 2 cash card belonging to 'Clara' ordered by with amount desc")
    void t13() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/getcards/Clara?page=0&size=2&sort=amount,desc"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        DocumentContext documentContext = JsonPath.parse(json);
        String contentJson = documentContext.read("$.content").toString();
        List<CashCard> contentList = objectMapper.readValue(contentJson, new TypeReference<>(){});

        assertThat(2).isEqualTo(contentList.size());
        assertEquals(22, contentList.get(0).getId());
        assertEquals(29.3, contentList.get(0).getAmount());

        assertEquals(5, contentList.get(1).getId());
        assertEquals(25.7, contentList.get(1).getAmount());
    }

    @Test
    @DisplayName("should return second page with 1 cash card belonging to 'Clara' with lowest amount")
    void t14() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/getcards/Clara?page=1&size=2&sort=amount,desc"))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        DocumentContext documentContext = JsonPath.parse(json);
        String contentJson = documentContext.read("$.content").toString();
        List<CashCard> contentList = objectMapper.readValue(contentJson, new TypeReference<>(){});

        assertThat(1).isEqualTo(contentList.size());
        assertEquals(14, contentList.get(0).getId());
        assertEquals(20.5, contentList.get(0).getAmount());
    }
}
