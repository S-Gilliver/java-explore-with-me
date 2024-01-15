package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto postUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        User user = UserMapper.createUser(newUserRequest);
        return UserMapper.createUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<UserDto> getAllUsers(List<Integer> ids, int from, int size) {
        if (ids.isEmpty()) {
            PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            return userRepository.findAll(pageRequest).map(UserMapper::createUserDto).getContent();
        }
        return userRepository.getAllUsersById(ids).stream().map(UserMapper::createUserDto).collect(Collectors.toList());


    }
}
