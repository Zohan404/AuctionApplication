package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT u FROM User u
        WHERE
            LOWER(CONCAT(
                u.firstName,
                ' ',
                CASE WHEN u.middleName IS NULL OR u.middleName = '' THEN '' ELSE CONCAT(u.middleName, ' ') END,
                u.lastName
            )) LIKE LOWER(CONCAT('%', TRIM(:title), '%'))
            AND (:country IS NULL OR u.location = :country)
""")
    Page<User> findFilteredUsers(String title, Country country, Pageable pageable);
    Optional<User> findByEmail(String email);
}
