package com.mag.app.user;

import java.util.List;
import java.util.Set;


import com.mag.app.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsers(
            String search,
            String profession,
            String country,
            String city,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        if (search == null) {
            search = "";
        }

        Sort sort;
        if (Set.of("id", "dateCreated").contains(sortBy)) {
            sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(Sort.Order.asc(sortBy))
                    : Sort.by(Sort.Order.desc(sortBy));
        } else {
            sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(Sort.Order.asc(sortBy).ignoreCase())
                    : Sort.by(Sort.Order.desc(sortBy).ignoreCase());
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.searchUsers(
                search,
                profession,
                country,
                city,
                pageable
        );
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public List<String> getFilterOptions(String filter) {
        return switch (filter) {
            case "profession" -> userRepository.getDistinctProfessions();
            case "country" -> userRepository.getDistinctCountries();
            case "city" -> userRepository.getDistinctCities();
            default -> throw new IllegalStateException("Unexpected value: " + filter);
        };
    }

    public User createUser(UserCreateRequestDto request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .profession(request.profession())
                .country(request.country())
                .city(request.city())
                .build();
        return userRepository.save(user);
    }
}

