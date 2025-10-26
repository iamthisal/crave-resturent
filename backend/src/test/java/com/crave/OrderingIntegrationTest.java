package com.crave;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:crave-test;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@AutoConfigureMockMvc
class OrderingIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  private JsonNode body(ResultActions result) throws Exception {
    return json.readTree(result.andReturn().getResponse().getContentAsString());
  }

  private MockHttpServletRequestBuilder withJson(
      MockHttpServletRequestBuilder request, Object value) throws Exception {
    return request.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(value));
  }

  private String register() throws Exception {
    JsonNode response =
        body(
            mvc.perform(
                    withJson(
                        post("/api/auth/register"),
                        Map.of(
                            "name",
                            "Test Customer",
                            "email",
                            UUID.randomUUID() + "@test.com",
                            "password",
                            "test123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.role").value("USER"))
                .andExpect(jsonPath("$.user.password").doesNotExist()));
    return response.get("token").asText();
  }

  private String adminToken() throws Exception {
    return body(mvc.perform(
                withJson(
                    post("/api/auth/login"),
                    Map.of("email", "admin@crave.com", "password", "admin123")))
            .andExpect(status().isOk()))
        .get("token")
        .asText();
  }

  private Map<String, Object> dish(boolean available) {
    return Map.of(
        "name",
        "Test dish",
        "description",
        "Fresh test dish",
        "price",
        125.50,
        "imageUrl",
        "",
        "category",
        "Test",
        "available",
        available);
  }

  private long createDish(String token, boolean available) throws Exception {
    return body(mvc.perform(
                withJson(post("/api/dishes"), dish(available))
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isCreated()))
        .get("id")
        .asLong();
  }

  private Map<String, Object> order(long dishId, int quantity) {
    return Map.of(
        "fullName",
        "Test Customer",
        "phone",
        "+94 771234567",
        "deliveryAddress",
        "10 Main Street",
        "note",
        "Ring the bell",
        "items",
        new Object[] {Map.of("dishId", dishId, "quantity", quantity)});
  }

  @Test
  void publicMenuAndSecurityRules() throws Exception {
    JsonNode menu = body(mvc.perform(get("/api/dishes")).andExpect(status().isOk()));
    assertThat(menu.size()).isGreaterThanOrEqualTo(6);
    mvc.perform(get("/api/orders")).andExpect(status().isUnauthorized());
    mvc.perform(get("/api/orders/my").header("Authorization", "Bearer broken"))
        .andExpect(status().isUnauthorized());
    String customer = register();
    mvc.perform(get("/api/orders").header("Authorization", "Bearer " + customer))
        .andExpect(status().isForbidden());
    mvc.perform(
            withJson(post("/api/dishes"), dish(true)).header("Authorization", "Bearer " + customer))
        .andExpect(status().isForbidden());
    mvc.perform(
            withJson(post("/api/orders"), order(1L, 1))
                .header("Authorization", "Bearer " + adminToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginRegistrationAndValidation() throws Exception {
    String email = UUID.randomUUID() + "@test.com";
    Map<String, String> account = Map.of("name", "Student", "email", email, "password", "test123");
    mvc.perform(withJson(post("/api/auth/register"), account)).andExpect(status().isCreated());
    mvc.perform(withJson(post("/api/auth/register"), account)).andExpect(status().isConflict());
    mvc.perform(withJson(post("/api/auth/login"), Map.of("email", email, "password", "wrong")))
        .andExpect(status().isUnauthorized());
    mvc.perform(
            withJson(
                post("/api/auth/login"),
                Map.of("email", email.toUpperCase(), "password", "test123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
    mvc.perform(
            withJson(
                post("/api/auth/register"), Map.of("name", "", "email", "bad", "password", "a")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").isString());
  }

  @Test
  void orderFlowTotalsOwnershipAndDishSnapshots() throws Exception {
    String admin = adminToken();
    String customer = register();
    long dishId = createDish(admin, true);
    JsonNode placed =
        body(
            mvc.perform(
                    withJson(post("/api/orders"), order(dishId, 2))
                        .header("Authorization", "Bearer " + customer))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total").value(251.0)));
    long orderId = placed.get("id").asLong();
    mvc.perform(
            withJson(put("/api/orders/" + orderId + "/status"), Map.of("status", "DELIVERED"))
                .header("Authorization", "Bearer " + admin))
        .andExpect(status().isBadRequest());
    for (String next : new String[] {"PREPARING", "DELIVERING", "DELIVERED"}) {
      mvc.perform(
              withJson(put("/api/orders/" + orderId + "/status"), Map.of("status", next))
                  .header("Authorization", "Bearer " + admin))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(next));
    }
    mvc.perform(delete("/api/dishes/" + dishId).header("Authorization", "Bearer " + admin))
        .andExpect(status().isNoContent());
    JsonNode mine =
        body(
            mvc.perform(get("/api/orders/my").header("Authorization", "Bearer " + customer))
                .andExpect(status().isOk()));
    assertThat(mine.size()).isEqualTo(1);
    assertThat(mine.get(0).get("items").get(0).get("dishName").asText()).isEqualTo("Test dish");
    JsonNode other =
        body(
            mvc.perform(get("/api/orders/my").header("Authorization", "Bearer " + register()))
                .andExpect(status().isOk()));
    assertThat(other.isEmpty()).isTrue();
    mvc.perform(get("/api/orders").header("Authorization", "Bearer " + admin))
        .andExpect(status().isOk());
  }

  @Test
  void unavailableDishesAndBadOrdersAreRejected() throws Exception {
    String customer = register();
    long dishId = createDish(adminToken(), false);
    mvc.perform(
            withJson(post("/api/orders"), order(dishId, 1))
                .header("Authorization", "Bearer " + customer))
        .andExpect(status().isBadRequest());
    mvc.perform(
            withJson(post("/api/orders"), order(dishId, 0))
                .header("Authorization", "Bearer " + customer))
        .andExpect(status().isBadRequest());
    mvc.perform(
            withJson(
                    post("/api/orders"),
                    Map.of(
                        "fullName",
                        "Student",
                        "phone",
                        "0771234567",
                        "deliveryAddress",
                        "Main Street",
                        "items",
                        new Object[] {}))
                .header("Authorization", "Bearer " + customer))
        .andExpect(status().isBadRequest());
    mvc.perform(
            withJson(post("/api/orders"), order(999999L, 1))
                .header("Authorization", "Bearer " + customer))
        .andExpect(status().isNotFound());
  }

  @Test
  void adminCanEditDishesAndCorsAllowsFrontend() throws Exception {
    String admin = adminToken();
    long dishId = createDish(admin, true);
    mvc.perform(
            withJson(put("/api/dishes/" + dishId), dish(false))
                .header("Authorization", "Bearer " + admin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(false));
    mvc.perform(
            options("/api/orders")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
  }
}
