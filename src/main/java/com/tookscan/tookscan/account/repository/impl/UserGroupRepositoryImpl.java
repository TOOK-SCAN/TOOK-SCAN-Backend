package com.tookscan.tookscan.account.repository.impl;

import com.nimbusds.jose.util.Pair;
import com.tookscan.tookscan.account.domain.UserGroup;
import com.tookscan.tookscan.account.repository.UserGroupRepository;
import com.tookscan.tookscan.account.repository.mysql.UserGroupJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserGroupRepositoryImpl implements UserGroupRepository {

    private final UserGroupJpaRepository userGroupJpaRepository;

    @Override
    public Set<Pair<UUID, Long>> findNotDuplicatedUserGroupInUserIdsAndGroupIds(List<UUID> userIds, List<Long> groupIds) {

        // 이미 등록된 UserGroup 조회
        Set<UserGroup> existingUserGroups = userGroupJpaRepository.findAllByUserIdInAndGroupIdIn(userIds, groupIds);

        // 이미 등록된 UserGroup에 대한 Pair 생성
        Set<Pair<UUID, Long>> existingPair = existingUserGroups.stream()
                .map(userGroup -> Pair.of(userGroup.getUser().getId(), userGroup.getGroup().getId()))
                .collect(Collectors.toSet());

        // 입력받은 전체 UserGroup에 대해 가능한 Pair 생성
        Set<Pair<UUID, Long>> allPair = userIds.stream()
                .flatMap(userId -> groupIds.stream().map(groupId -> Pair.of(userId, groupId)))
                .collect(Collectors.toSet());

        // 기존 Pairs에 존재하지 않는 것만 필터링
        return allPair.stream()
                .filter(pair -> existingPair.stream()
                        .noneMatch(existing -> existing.getLeft().equals(pair.getLeft())
                                && existing.getRight().equals(pair.getRight())))
                .collect(Collectors.toSet());
    }

    @Override
    public void saveAll(List<UserGroup> userGroups) {
        userGroupJpaRepository.saveAll(userGroups);
    }
}
