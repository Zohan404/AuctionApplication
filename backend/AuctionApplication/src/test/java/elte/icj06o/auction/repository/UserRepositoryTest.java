package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.Role;
import elte.icj06o.auction.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User testUser1 = new User(
                "John",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(90, 1, 1), // February 1, 1990
                Country.United_States,
                "https://example.com/profile.jpg",
                Set.of(Role.USER)
        );

        User testUser2 = new User(
                "Jane",
                "Mary",
                "Smith",
                "password456",
                "jane.smith@example.com",
                new Date(95, 5, 15), // June 15, 1995
                Country.United_Kingdom,
                "https://example.com/profile2.jpg",
                Set.of(Role.USER, Role.ADVERTISER)
        );

        userRepository.save(testUser1);
        userRepository.save(testUser2);
    }

    @Test
    public void testFindByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        assertEquals("Doe", foundUser.get().getLastName());
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindFilteredUsersByName() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> usersFoundByFirstName = userRepository.findFilteredUsers("John", null, pageable);

        assertEquals(1, usersFoundByFirstName.getTotalElements());
        assertEquals("John", usersFoundByFirstName.getContent().get(0).getFirstName());

        Page<User> usersFoundByLastName = userRepository.findFilteredUsers("Smith", null, pageable);

        assertEquals(1, usersFoundByLastName.getTotalElements());
        assertEquals("Jane", usersFoundByLastName.getContent().get(0).getFirstName());

        Page<User> usersFoundByMiddleName = userRepository.findFilteredUsers("Mary", null, pageable);

        assertEquals(1, usersFoundByMiddleName.getTotalElements());
        assertEquals("Jane", usersFoundByMiddleName.getContent().get(0).getFirstName());

        Page<User> usersFoundByFullName = userRepository.findFilteredUsers("Jane Mary Smith", null, pageable);

        assertEquals(1, usersFoundByFullName.getTotalElements());
        assertEquals("Jane", usersFoundByFullName.getContent().get(0).getFirstName());

        Page<User> usersFoundByPartialName = userRepository.findFilteredUsers("ane Ma", null, pageable);

        assertEquals(1, usersFoundByPartialName.getTotalElements());
        assertEquals("Jane", usersFoundByPartialName.getContent().get(0).getFirstName());
    }

    @Test
    public void testFindFilteredUsersByCountry() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> usersFromUS = userRepository.findFilteredUsers("", Country.United_States, pageable);

        assertEquals(1, usersFromUS.getTotalElements());
        assertEquals("John", usersFromUS.getContent().get(0).getFirstName());

        Page<User> usersFromUK = userRepository.findFilteredUsers("", Country.United_Kingdom, pageable);

        assertEquals(1, usersFromUK.getTotalElements());
        assertEquals("Jane", usersFromUK.getContent().get(0).getFirstName());
    }

    @Test
    public void testFindFilteredUsersByNameAndCountry() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> users = userRepository.findFilteredUsers("John", Country.United_States, pageable);

        assertEquals(1, users.getTotalElements());
        assertEquals("John", users.getContent().get(0).getFirstName());

        Page<User> emptyResult = userRepository.findFilteredUsers("John", Country.United_Kingdom, pageable);

        assertEquals(0, emptyResult.getTotalElements());
    }
}
