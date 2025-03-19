package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Sql(scripts = "/currentUserShouldBeReturned.sql")
  @Test
  void currentUserShouldBeReturned() throws Exception {
    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"))
        .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
        .andExpect(jsonPath("$.currency").value("RUB"))
        .andExpect(jsonPath("$.photo").isNotEmpty())
        .andExpect(jsonPath("$.photoSmall").isNotEmpty());
  }


  @Sql(scripts = "/allUsersShouldBeReturned.sql")
  @Test
  void allUsersShouldBeReturned() throws Exception {
    mockMvc.perform(get("/internal/users/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "user1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray()) // Проверяем, что ответ — массив
            .andExpect(jsonPath("$[0].username").value("user2"))
            .andExpect(jsonPath("$[0].fullname").value("Jane Smith"))
            .andExpect(jsonPath("$[0].currency").value("RUB"))
            .andExpect(jsonPath("$[1].username").value("user3"))
            .andExpect(jsonPath("$[1].fullname").value("Ivan Ivanov"))
            .andExpect(jsonPath("$[1].currency").value("RUB"))
            .andExpect(jsonPath("$[3].username").value("user4"))
            .andExpect(jsonPath("$[3].fullname").value("Emily Davis"))
            .andExpect(jsonPath("$[3].currency").value("RUB"))
    ;
  }
}