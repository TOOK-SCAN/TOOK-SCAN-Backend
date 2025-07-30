package com.tookscan.tookscan.account.application.service;

import com.nimbusds.jose.util.Pair;
import com.tookscan.tookscan.account.application.usecase.CreateAdminUserGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.UserGroup;
import com.tookscan.tookscan.account.domain.service.UserGroupService;
import com.tookscan.tookscan.account.presentation.dto.request.CreateAdminUserGroupRequestDto;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.account.repository.UserGroupRepository;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminUserGroupService implements CreateAdminUserGroupUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAdminUserGroupService.class);

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final UserGroupService userGroupService;

    @Override
    @Transactional
    public void execute(CreateAdminUserGroupRequestDto requestDto) {
        StructuredLoggerUtil.info(log)
                .message("[Account] Admin create user group process started")
                .details(Map.of(
                        "user_ids", requestDto.userIds(),
                        "group_ids", requestDto.groupIds()
                ))
                .log();

        // 사용자의 요청 중, 이미 등록된 UserGroup을 제외한 UserId, GroupId Pair 조회
        Set<Pair<UUID, Long>> objectPairs = userGroupRepository.findNotDuplicatedUserGroupInUserIdsAndGroupIds(requestDto.userIds(), requestDto.groupIds());

        StructuredLoggerUtil.debug(log)
                .message("[Account] Target pairs retrieved after excluding duplicates")
                .field("target_pair_count", objectPairs.size())
                .log();

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

        StructuredLoggerUtil.info(log)
                .message("[Account] Admin user group created successfully")
                .details(Map.of(
                        "user_group_ids", userGroups.stream().map(UserGroup::getId).toList(),
                        "user_group_count", userGroups.size()
                ))
                .log();
    }
}
