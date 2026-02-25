package com.liverpool.liverpooltest.domain.port.in;

import com.liverpool.liverpooltest.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserUseCase {

    record CreateUserCommand(
            String name,
            String paternalLastName,
            String maternalLastName,
            String email,
            String postalCode) {}

    record UpdateUserCommand(
            String name,
            String paternalLastName,
            String maternalLastName,
            String email,
            String postalCode) {}

    User createUser(CreateUserCommand command);

    Page<User> getAllUsers(Pageable pageable);

    User getUserById(String id);

    User updateUser(String id, UpdateUserCommand command);

    void deleteUser(String id);
}
