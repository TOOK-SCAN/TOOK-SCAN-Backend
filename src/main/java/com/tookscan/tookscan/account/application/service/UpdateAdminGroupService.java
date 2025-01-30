package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.dto.request.UpdateAdminGroupRequestDto;
import com.tookscan.tookscan.account.application.usecase.UpdateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.repository.GroupRepository;
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
    public void execute(UpdateAdminGroupRequestDto requestDto, Long groupId) {
        // 그룹 조회
        Group group = groupRepository.findByIdOrElseThrow(groupId);

        // 그룹 정보 수정
        group = groupService.updateGroupName(group, requestDto.name());
        groupRepository.save(group);
    }
}
