package com.mag.app.user;

import com.mag.app.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        User user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .profession("Engineer")
                .city("New York")
                .country("United States")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertSame(user, result);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        List<User> users = List.of(User.builder().firstName("John").build(), User.builder().firstName("Jane").build());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertEquals(users, result);
    }

    @Test
    void getUsers_shouldPassCorrectSearchParametersAndPageable() {
        Page<User> pageResult = new PageImpl<>(List.of());

        when(userRepository.searchUsers(anyString(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResult);

        Page<User> result = userService.getUsers(
                "john",
                "Developer",
                "United States",
                "Atlanta",
                1,
                20,
                "dateCreated",
                "asc"
        );

        assertSame(pageResult, result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).searchUsers(
                eq("john"),
                eq("Developer"),
                eq("United States"),
                eq("Atlanta"),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor("dateCreated");

        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void getUsers_shouldUseEmptySearch_whenSearchIsNull() {
        Page<User> pageResult = new PageImpl<>(List.of());

        when(userRepository.searchUsers(anyString(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResult);

        userService.getUsers(
                null,
                "Developer",
                "United States",
                "Atlanta",
                0,
                10,
                "dateCreated",
                "desc"
        );

        verify(userRepository).searchUsers(
                eq(""),
                eq("Developer"),
                eq("United States"),
                eq("Atlanta"),
                any(Pageable.class)
        );
    }

    @Test
    void getUsers_shouldSortIdAscending_withoutIgnoreCase() {
        Page<User> pageResult = new PageImpl<>(List.of());

        when(userRepository.searchUsers(anyString(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResult);

        userService.getUsers(
                "",
                null,
                null,
                null,
                0,
                10,
                "id",
                "asc"
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).searchUsers(
                eq(""),
                isNull(),
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("id");

        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
        assertFalse(order.isIgnoreCase());
    }



    @Test
    void getUsers_shouldSortStringFieldDescending_withIgnoreCase() {
        Page<User> pageResult = new PageImpl<>(List.of());

        when(userRepository.searchUsers(
                anyString(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(pageResult);

        userService.getUsers(
                "",
                null,
                null,
                null,
                0,
                10,
                "firstName",
                "desc"
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).searchUsers(anyString(), isNull(), isNull(), isNull(), pageableCaptor.capture());

        Sort.Order order =
                pageableCaptor.getValue().getSort().getOrderFor("firstName");

        assertEquals(Sort.Direction.DESC, order.getDirection());
        assertTrue(order.isIgnoreCase());
    }

    @Test
    void deleteUserById_shouldDeleteUser() {
        userService.deleteUserById(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void getFilterOptions_shouldReturnProfessions() {
        List<String> professions = List.of("Doctor", "Engineer");

        when(userRepository.getDistinctProfessions()).thenReturn(professions);

        List<String> result = userService.getFilterOptions("profession");

        assertEquals(professions, result);
    }

    @Test
    void getFilterOptions_shouldThrowException_forInvalidFilter() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.getFilterOptions("invalid")
        );

        assertEquals("Unexpected value: invalid", exception.getMessage());
    }

    @Test
    void createUser_shouldMapRequestToUserAndSave() {
        UserCreateRequestDto request = new UserCreateRequestDto(
                "John",
                "Doe",
                "john@example.com",
                "Developer",
                "United States",
                "Atlanta"
        );

        User savedUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .profession("Developer")
                .country("United States")
                .city("Atlanta")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = userService.createUser(request);

        assertSame(savedUser, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals("John", userToSave.getFirstName());
        assertEquals("Doe", userToSave.getLastName());
        assertEquals("john@example.com", userToSave.getEmail());
        assertEquals("Developer", userToSave.getProfession());
        assertEquals("United States", userToSave.getCountry());
        assertEquals("Atlanta", userToSave.getCity());
    }
}