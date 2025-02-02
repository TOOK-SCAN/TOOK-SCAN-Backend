package com.tookscan.tookscan.account.repository;

import com.tookscan.tookscan.account.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User findByIdOrElseThrow(UUID userId);

    List<User> findByIds(List<UUID> userIds);

    void save(User user);

    User findByPhoneNumberAndNameOrElseThrow(String phoneNumber, String name);

    List<User> findByIdsWithDetails(List<UUID> userIds);

    Page<UUID> findUserIdsByFilters(String searchType, String search, Long groupId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
