package com.tookscan.tookscan.account.repository.mysql;

import com.tookscan.tookscan.account.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumberAndNameAndDeletedAtIsNull(String phoneNumber, String name);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userGroups " +
            "WHERE u.id IN :userIds")
    List<User> findUserByIdsWithDetails(@Param("userIds") List<UUID> userIds);

    @Query("SELECT u From User u " +
            "WHERE u.id IN :userIds")
    List<User> findUserByIds(@Param("userIds") List<UUID> userIds);

    Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
