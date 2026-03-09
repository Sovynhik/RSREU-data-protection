package ru.rsreu.sovynhik.pract_03.service;

import ru.rsreu.sovynhik.pract_03.model.User;
import ru.rsreu.sovynhik.pract_03.repository.UserRepository;
import java.util.Optional;

/**
 * Сервис аутентификации пользователей в системе дискреционного управления доступом.
 * Отвечает за идентификацию пользователей по имени.
 */
public class AuthenticationService {
    private final UserRepository userRepository;

    /**
     * Создает сервис аутентификации с указанным репозиторием пользователей.
     *
     * @param userRepository репозиторий для поиска пользователей
     */
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Выполняет аутентификацию пользователя по имени.
     * Поиск выполняется без учета регистра, игнорируются начальные и конечные пробелы.
     * Пустые или null-значения считаются невалидными и возвращают пустой результат.
     *
     * @param username имя пользователя для аутентификации
     * @return {@link Optional}, содержащий найденного пользователя, или пустой {@link Optional},
     *         если пользователь не найден или имя пустое
     */
    public Optional<User> authenticate(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByName(username.trim());
    }
}