package me.darkakyloff.subscriptionservice.service;

import me.darkakyloff.subscriptionservice.dto.UserDto;
import me.darkakyloff.subscriptionservice.exception.ResourceNotFoundException;
import me.darkakyloff.subscriptionservice.exception.UserAlreadyExistsException;
import me.darkakyloff.subscriptionservice.model.User;
import me.darkakyloff.subscriptionservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService
{

    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDto userDto)
    {
        if (userRepository.existsByEmail(userDto.getEmail()))
        {
            log.error("Пользователь с email {} уже существует", userDto.getEmail());
            throw new UserAlreadyExistsException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        log.info("Создание нового пользователя с email: {}", userDto.getEmail());
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Пользователь с ID {} успешно создан", savedUser.getId());

        return mapToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id)
    {
        log.info("Поиск пользователя с ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        return mapToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto)
    {
        log.info("Обновление пользователя с ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));

        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail()))
        {
            log.error("Невозможно обновить пользователя. Email {} уже используется", userDto.getEmail());
            throw new UserAlreadyExistsException("Email " + userDto.getEmail() + " уже используется другим пользователем");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) user.setPassword(userDto.getPassword());

        User updatedUser = userRepository.save(user);
        log.info("Пользователь с ID {} успешно обновлен", updatedUser.getId());

        return mapToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id)
    {
        log.info("Удаление пользователя с ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));

        userRepository.delete(user);
        log.info("Пользователь с ID {} успешно удален", id);
    }

    private UserDto mapToDto(User user)
    {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
//                .password(user.getPassword())
                .build();
    }
}