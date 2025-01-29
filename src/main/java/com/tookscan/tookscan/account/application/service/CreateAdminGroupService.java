package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.dto.request.CreateAdminGroupRequestDto;
import com.tookscan.tookscan.account.application.usecase.CreateAdminGroupUseCase;
import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.domain.service.GroupService;
import com.tookscan.tookscan.account.repository.GroupRepository;
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
    public void execute(CreateAdminGroupRequestDto requestDto) {

        // 중복 그룹명 체크
        boolean isExists = groupRepository.existsByName(requestDto.name());

        // 그룹 생성
        Group group = groupService.createGroup(requestDto.name(), isExists);

        groupRepository.save(group);
    }
}
