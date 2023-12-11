package ru.practicum.ewm.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUser);

    List<UserDto> getUsers(List<Long> ids, PageRequest page);

    void removeUserById(Long userId);

    User getUserByIdForService(Long userId);
}
