package com.tookscan.tookscan.account.repository.mysql;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.mysql.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {

    Optional<User> findByPhoneNumberAndName(String phoneNumber, String name);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userGroups " +
            "WHERE u.id IN :userIds")
    List<User> findUserByIdsWithDetails(@Param("userIds") List<UUID> userIds);

}
