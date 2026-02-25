package com.liverpool.liverpooltest.infrastructure.adapter.in.rest;

import com.liverpool.liverpooltest.domain.port.in.UserUseCase;
import com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto.CreateUserRequest;
import com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto.PageResponse;
import com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto.UpdateUserRequest;
import com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User CRUD with COPOMEX address integration")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "Creates a new user and fetches their address from COPOMEX using the postal code")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or postal code not found"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        UserUseCase.CreateUserCommand command = new UserUseCase.CreateUserCommand(
                request.name(),
                request.paternalLastName(),
                request.maternalLastName(),
                request.email(),
                request.postalCode()
        );
        return UserResponse.from(userUseCase.createUser(command));
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns a paginated list of users. Supports ?page, ?size and ?sort query params")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public PageResponse<UserResponse> getAllUsers(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return PageResponse.from(userUseCase.getAllUsers(pageable).map(UserResponse::from));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse getUserById(@PathVariable String id) {
        return UserResponse.from(userUseCase.getUserById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update user", description = "Updates only the provided fields. Omitted fields keep their current value")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or postal code not found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserUseCase.UpdateUserCommand command = new UserUseCase.UpdateUserCommand(
                request.name(),
                request.paternalLastName(),
                request.maternalLastName(),
                request.email(),
                request.postalCode()
        );
        return UserResponse.from(userUseCase.updateUser(id, command));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(@PathVariable String id) {
        userUseCase.deleteUser(id);
    }
}
