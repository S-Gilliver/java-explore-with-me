package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserControllerAdmin {

    public final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUser) {
        log.info("POST user {}", newUser);
        return userService.createUser(newUser);
    }

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                  @Min(0) @RequestParam(defaultValue = "0") int from,
                                  @Min(0) @RequestParam(defaultValue = "10") int size) {
        PageRequest page = PageRequest.of(from / size, size);
        log.info("GET users");
        return userService.getUsers(ids, page);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserId(@PathVariable Long userId) {
        log.info("DELETE user by id {}", userId);
        userService.removeUserById(userId);
    }
}
