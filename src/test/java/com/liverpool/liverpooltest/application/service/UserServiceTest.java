package com.liverpool.liverpooltest.application.service;

import com.liverpool.liverpooltest.domain.exception.UserNotFoundException;
import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.model.User;
import com.liverpool.liverpooltest.domain.port.in.UserUseCase;
import com.liverpool.liverpooltest.domain.port.out.SepomexPort;
import com.liverpool.liverpooltest.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private SepomexPort sepomexPort;

    @InjectMocks
    private UserService userService;

    private static final Address ADDRESS = Address.builder()
            .postalCode("06600")
            .municipality("Cuauhtémoc")
            .state("Ciudad de México")
            .city("Ciudad de México")
            .neighborhoods(List.of("Juárez", "Roma Norte"))
            .country("México")
            .build();

    private static final User USER = User.builder()
            .id("123")
            .name("Juan")
            .paternalLastName("Pérez")
            .maternalLastName("García")
            .email("juan@example.com")
            .address(ADDRESS)
            .build();

    @Test
    void createUser_shouldFetchAddressAndSaveUser() {
        UserUseCase.CreateUserCommand command = new UserUseCase.CreateUserCommand(
                "Juan", "Pérez", "García", "juan@example.com", "06600");

        when(sepomexPort.getAddressByPostalCode("06600")).thenReturn(ADDRESS);
        when(userRepositoryPort.save(any(User.class))).thenReturn(USER);

        User result = userService.createUser(command);

        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getEmail()).isEqualTo("juan@example.com");
        verify(sepomexPort).getAddressByPostalCode("06600");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepositoryPort.findAll(pageable)).thenReturn(new PageImpl<>(List.of(USER)));

        Page<User> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("123");
    }

    @Test
    void getAllUsers_shouldReturnEmptyPage_whenNoUsersExist() {
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepositoryPort.findAll(pageable)).thenReturn(Page.empty());

        Page<User> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepositoryPort.findById("123")).thenReturn(Optional.of(USER));

        User result = userService.getUserById("123");

        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenNotExists() {
        when(userRepositoryPort.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById("999"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateUser_shouldUpdateAllProvidedFields() {
        UserUseCase.UpdateUserCommand command = new UserUseCase.UpdateUserCommand(
                "Juan Updated", "Pérez", "García", "nuevo@example.com", "06600");
        User updatedUser = USER.toBuilder().name("Juan Updated").email("nuevo@example.com").build();

        when(userRepositoryPort.findById("123")).thenReturn(Optional.of(USER));
        when(sepomexPort.getAddressByPostalCode("06600")).thenReturn(ADDRESS);
        when(userRepositoryPort.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser("123", command);

        assertThat(result.getName()).isEqualTo("Juan Updated");
        verify(sepomexPort).getAddressByPostalCode("06600");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void updateUser_shouldKeepExistingFields_whenNullsProvided() {
        UserUseCase.UpdateUserCommand command = new UserUseCase.UpdateUserCommand(
                "Juan Updated", null, null, null, null);

        when(userRepositoryPort.findById("123")).thenReturn(Optional.of(USER));
        when(userRepositoryPort.save(any(User.class))).thenReturn(USER.toBuilder().name("Juan Updated").build());

        User result = userService.updateUser("123", command);

        assertThat(result.getName()).isEqualTo("Juan Updated");
        verifyNoInteractions(sepomexPort);
    }

    @Test
    void updateUser_shouldThrowUserNotFoundException_whenNotExists() {
        UserUseCase.UpdateUserCommand command = new UserUseCase.UpdateUserCommand(
                "Juan", "Pérez", "García", "juan@example.com", "06600");

        when(userRepositoryPort.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser("999", command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");

        verifyNoInteractions(sepomexPort);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        when(userRepositoryPort.existsById("123")).thenReturn(true);

        userService.deleteUser("123");

        verify(userRepositoryPort).deleteById("123");
    }

    @Test
    void deleteUser_shouldThrowUserNotFoundException_whenNotExists() {
        when(userRepositoryPort.existsById("999")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser("999"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepositoryPort, never()).deleteById(any());
    }
}
