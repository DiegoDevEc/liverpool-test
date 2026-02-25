package com.liverpool.liverpooltest.domain.port.out;

import com.liverpool.liverpooltest.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Page<User> findAll(Pageable pageable);
    void deleteById(String id);
    boolean existsById(String id);
}
