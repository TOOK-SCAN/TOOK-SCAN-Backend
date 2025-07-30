package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.UpdateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.presentation.dto.request.UpdateAdminGroupRequestDto;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAdminGroupService implements UpdateAdminGroupUseCase {

    private final GroupRepository groupRepository;

    private final GroupService groupService;

    @Override
    @Transactional
    public void execute(UpdateAdminGroupRequestDto requestDto, Long groupId) {
        StructuredLoggerUtil.info(log)
                .message("[Account] Admin update group name process started")
                .details(Map.of(
                        "group_id", groupId
                ))
                .log();

        // 그룹 조회
        Group group = groupRepository.findByIdOrElseThrow(groupId);
        String oldGroupName = group.getName();

        // 중복 그룹명 체크
        boolean isExists = groupRepository.existsByName(requestDto.name());

        // 그룹 정보 수정
        group = groupService.updateGroupName(group, requestDto.name(), isExists);
        groupRepository.save(group);

        StructuredLoggerUtil.info(log)
                .message("[Account] Admin group name updated successfully")
                .details(Map.of(
                        "group_id", group.getId()
                ))
                .log();
    }
}
