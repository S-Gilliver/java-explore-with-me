package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {
    UserDto postUser(NewUserRequest newUserRequest);

    void deleteUser(int userId);

    List<UserDto> getAllUsers(List<Integer> ids, int from, int size);
}
