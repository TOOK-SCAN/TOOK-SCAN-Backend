package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.DeleteAdminGroupUseCase;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAdminGroupService implements DeleteAdminGroupUseCase {

    private final GroupRepository groupRepository;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Account",
        action = "delete group",
        userType = "Admin"
    )
    public void execute(Long groupId) {
        groupRepository.deleteByIdOrElseThrow(groupId);

        LogContext.put("group_id", groupId);
    }

}
