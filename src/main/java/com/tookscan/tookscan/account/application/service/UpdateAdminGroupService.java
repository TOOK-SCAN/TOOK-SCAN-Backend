package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.UpdateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.presentation.dto.request.UpdateAdminGroupRequestDto;
import com.tookscan.tookscan.account.repository.GroupRepository;
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
        log.atInfo()
            .addKeyValue("group_id", groupId)
            .log("[Account] Admin update group name process started");

        // 그룹 조회
        Group group = groupRepository.findByIdOrElseThrow(groupId);
        String oldGroupName = group.getName();

        // 중복 그룹명 체크
        boolean isExists = groupRepository.existsByName(requestDto.name());

        // 그룹 정보 수정
        group = groupService.updateGroupName(group, requestDto.name(), isExists);
        groupRepository.save(group);

        log.atInfo()
            .addKeyValue("group_id", group.getId())
            .log("[Account] Admin group name updated successfully");
    }
}
