// Создаем CustomUserDetailsManager.java
package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.repository.UserRepository;

@Service
public class CustomUserDetailsManager implements UserDetailsManager {

        private final UserRepository userRepository;

    public CustomUserDetailsManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь '%s' не найден", username)));
    }

    @Override
    public void createUser(UserDetails user) {
        // Можно реализовать или оставить пустым, если регистрация через AuthService
        throw new UnsupportedOperationException("Создание пользователя через UserDetailsManager не поддерживается");
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Обновление пользователя через UserDetailsManager не поддерживается");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Удаление пользователя через UserDetailsManager не поддерживается");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Смена пароля через UserDetailsManager не поддерживается");
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }
}