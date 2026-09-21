package com.mag.app.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>{

    @Query("""
        SELECT DISTINCT city FROM User ORDER BY city ASC
    """)
    List<String> getDistinctCities();

    @Query("""
        SELECT DISTINCT country FROM User ORDER BY country ASC
    """)
    List<String> getDistinctCountries();
    @Query("""
        SELECT DISTINCT profession FROM User ORDER BY profession ASC
    """)
    List<String> getDistinctProfessions();

    @Query("""
        SELECT u FROM User u
        WHERE (:search IS NULL OR
               LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:profession IS NULL OR u.profession = :profession)
          AND (:country IS NULL OR u.country = :country)
          AND (:city IS NULL OR u.city = :city)
    """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("profession") String profession,
            @Param("country") String country,
            @Param("city") String city,
            Pageable pageable
    );

}

