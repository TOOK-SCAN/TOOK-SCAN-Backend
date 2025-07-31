package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.UpdateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.presentation.dto.request.UpdateAdminGroupRequestDto;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminGroupService implements UpdateAdminGroupUseCase {

    private final GroupRepository groupRepository;

    private final GroupService groupService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Account",
        action = "update group",
        userType = "Admin"
    )
    public void execute(UpdateAdminGroupRequestDto requestDto, Long groupId) {
        // 그룹 조회
        Group group = groupRepository.findByIdOrElseThrow(groupId);

        // 중복 그룹명 체크
        boolean isExists = groupRepository.existsByName(requestDto.name());

        // 그룹 정보 수정
        group = groupService.updateGroupName(group, requestDto.name(), isExists);
        groupRepository.save(group);

        LogContext.put("group_id", group.getId());
        LogContext.put("updated_group_name", group.getName());
    }
}
