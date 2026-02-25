package com.liverpool.liverpooltest.infrastructure.adapter.in.rest;

import com.liverpool.liverpooltest.domain.exception.PostalCodeNotFoundException;
import com.liverpool.liverpooltest.domain.exception.UserNotFoundException;
import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.model.User;
import com.liverpool.liverpooltest.domain.port.in.UserUseCase;
import com.liverpool.liverpooltest.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userUseCase;

    private static final Address ADDRESS = Address.builder()
            .postalCode("06600")
            .municipality("Cuauhtémoc")
            .state("Ciudad de México")
            .city("Ciudad de México")
            .neighborhoods(List.of("Juárez"))
            .country("México")
            .build();

    private static final User USER = User.builder()
            .id("abc123")
            .name("Juan")
            .paternalLastName("Pérez")
            .maternalLastName("García")
            .email("juan@example.com")
            .address(ADDRESS)
            .build();


    @Test
    void createUser_shouldReturn201_whenValidRequest() throws Exception {
        when(userUseCase.createUser(any())).thenReturn(USER);

        String body = """
                {
                    "name": "Juan",
                    "paternalLastName": "Pérez",
                    "maternalLastName": "García",
                    "email": "juan@example.com",
                    "postalCode": "06600"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.address.municipality").value("Cuauhtémoc"));
    }

    @Test
    void createUser_shouldReturn400_whenNameIsBlank() throws Exception {
        String body = """
                {
                    "name": "",
                    "paternalLastName": "Pérez",
                    "email": "juan@example.com",
                    "postalCode": "06600"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createUser_shouldReturn400_whenEmailIsInvalid() throws Exception {
        String body = """
                {
                    "name": "Juan",
                    "paternalLastName": "Pérez",
                    "email": "not-an-email",
                    "postalCode": "06600"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_shouldReturn400_whenPostalCodeHasWrongFormat() throws Exception {
        String body = """
                {
                    "name": "Juan",
                    "paternalLastName": "Pérez",
                    "email": "juan@example.com",
                    "postalCode": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_shouldReturn400_whenPostalCodeNotFound() throws Exception {
        when(userUseCase.createUser(any())).thenThrow(new PostalCodeNotFoundException("99999"));

        String body = """
                {
                    "name": "Juan",
                    "paternalLastName": "Pérez",
                    "email": "juan@example.com",
                    "postalCode": "99999"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No address found for postal code: 99999"));
    }

    @Test
    void getAllUsers_shouldReturn200WithPagedContent() throws Exception {
        when(userUseCase.getAllUsers(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(USER)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("abc123"))
                .andExpect(jsonPath("$.content[0].email").value("juan@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageable").doesNotExist());
    }

    @Test
    void getAllUsers_shouldReturn200WithEmptyPage() throws Exception {
        when(userUseCase.getAllUsers(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getAllUsers_shouldSupportPaginationQueryParams() throws Exception {
        when(userUseCase.getAllUsers(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(USER)));

        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("abc123"))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void getUserById_shouldReturn200_whenUserExists() throws Exception {
        when(userUseCase.getUserById("abc123")).thenReturn(USER);

        mockMvc.perform(get("/api/v1/users/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    void getUserById_shouldReturn404_whenUserNotFound() throws Exception {
        when(userUseCase.getUserById("999")).thenThrow(new UserNotFoundException("999"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void updateUser_shouldReturn200_whenFullRequest() throws Exception {
        when(userUseCase.updateUser(eq("abc123"), any())).thenReturn(USER);

        String body = """
                {
                    "name": "Juan",
                    "paternalLastName": "Pérez",
                    "email": "juan@example.com",
                    "postalCode": "06600"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void updateUser_shouldReturn200_whenPartialRequest() throws Exception {
        when(userUseCase.updateUser(eq("abc123"), any())).thenReturn(USER);

        String body = """
                {
                    "name": "Solo nombre actualizado"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldReturn400_whenEmailFormatIsInvalid() throws Exception {
        String body = """
                {
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturn404_whenUserNotFound() throws Exception {
        when(userUseCase.updateUser(eq("999"), any())).thenThrow(new UserNotFoundException("999"));

        String body = """
                {
                    "name": "Juan"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturn204_whenUserExists() throws Exception {
        doNothing().when(userUseCase).deleteUser("abc123");

        mockMvc.perform(delete("/api/v1/users/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("999")).when(userUseCase).deleteUser("999");

        mockMvc.perform(delete("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
