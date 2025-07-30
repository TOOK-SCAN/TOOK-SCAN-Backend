package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.CreateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.presentation.dto.request.CreateAdminGroupRequestDto;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminGroupService implements CreateAdminGroupUseCase {

    private final GroupRepository groupRepository;

    private final GroupService groupService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Account",
        action = "create group",
            userType = "Admin"
    )
    public void execute(CreateAdminGroupRequestDto requestDto) {
        // 중복 그룹명 체크
        boolean isExists = groupRepository.existsByName(requestDto.name());

        // 그룹 생성
        Group group = groupService.createGroup(requestDto.name(), isExists);

        groupRepository.save(group);

        LogContext.put("group_id", group.getId());
        LogContext.put("group_name", group.getName());
    }
}
