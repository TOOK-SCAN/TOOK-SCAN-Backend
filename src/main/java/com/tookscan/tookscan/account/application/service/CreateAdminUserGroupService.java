package com.tookscan.tookscan.account.application.service;

import com.nimbusds.jose.util.Pair;
import com.tookscan.tookscan.account.presentation.dto.request.CreateAdminUserGroupRequestDto;
import com.tookscan.tookscan.account.application.usecase.CreateAdminUserGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.UserGroup;
import com.tookscan.tookscan.account.domain.service.UserGroupService;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.account.repository.UserGroupRepository;
import com.tookscan.tookscan.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateAdminUserGroupService implements CreateAdminUserGroupUseCase {

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final UserGroupService userGroupService;

    @Override
    @Transactional
    public void execute(CreateAdminUserGroupRequestDto requestDto) {

        // 사용자의 요청 중, 이미 등록된 UserGroup을 제외한 UserId, GroupId Pair 조회
        Set<Pair<UUID, Long>> objectPairs = userGroupRepository.findNotDuplicatedUserGroupInUserIdsAndGroupIds(requestDto.userIds(), requestDto.groupIds());

        List<UUID> userIds = objectPairs.stream()
                .map(Pair::getLeft)
                .distinct()
                .toList();

        List<Long> groupIds = objectPairs.stream()
                .map(Pair::getRight)
                .distinct()
                .toList();

        Map<UUID, User> userMap = userRepository.findByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));


        Map<Long, Group> groupMap = groupRepository.findByIds(groupIds).stream()
                .collect(Collectors.toMap(Group::getId, group -> group));

        List<UserGroup> userGroups = objectPairs.stream()
                .map(pair -> {
                    User user = userMap.get(pair.getLeft());
                    Group group = groupMap.get(pair.getRight());

                    return userGroupService.createUserGroup(user, group);

                })
                .toList();

        userGroupRepository.saveAll(userGroups);
    }
}
