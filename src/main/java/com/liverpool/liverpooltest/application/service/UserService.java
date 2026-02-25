package com.liverpool.liverpooltest.application.service;

import com.liverpool.liverpooltest.domain.exception.UserNotFoundException;
import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.model.User;
import com.liverpool.liverpooltest.domain.port.in.UserUseCase;
import com.liverpool.liverpooltest.domain.port.out.SepomexPort;
import com.liverpool.liverpooltest.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SepomexPort sepomexPort;

    @Override
    public User createUser(CreateUserCommand command) {
        log.info("Creating user with email: {}", command.email());
        Address address = sepomexPort.getAddressByPostalCode(command.postalCode());
        User user = User.builder()
                .name(command.name())
                .paternalLastName(command.paternalLastName())
                .maternalLastName(command.maternalLastName())
                .email(command.email())
                .address(address)
                .build();
        User saved = userRepositoryPort.save(user);
        log.info("User created with id: {}", saved.getId());
        return saved;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Fetching users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepositoryPort.findAll(pageable);
    }

    @Override
    public User getUserById(String id) {
        log.debug("Fetching user with id: {}", id);
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException(id);
                });
    }

    @Override
    public User updateUser(String id, UpdateUserCommand command) {
        log.info("Updating user with id: {}", id);
        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException(id);
                });
        Address address = command.postalCode() != null
                ? sepomexPort.getAddressByPostalCode(command.postalCode())
                : existing.getAddress();
        User updated = existing.toBuilder()
                .name(command.name() != null ? command.name() : existing.getName())
                .paternalLastName(command.paternalLastName() != null ? command.paternalLastName() : existing.getPaternalLastName())
                .maternalLastName(command.maternalLastName() != null ? command.maternalLastName() : existing.getMaternalLastName())
                .email(command.email() != null ? command.email() : existing.getEmail())
                .address(address)
                .build();
        User saved = userRepositoryPort.save(updated);
        log.info("User updated with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepositoryPort.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new UserNotFoundException(id);
        }
        userRepositoryPort.deleteById(id);
        log.info("User deleted with id: {}", id);
    }
}
