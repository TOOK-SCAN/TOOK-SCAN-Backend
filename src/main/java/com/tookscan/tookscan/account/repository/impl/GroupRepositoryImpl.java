package com.tookscan.tookscan.account.repository.impl;

import com.tookscan.tookscan.account.domain.Group;
import com.tookscan.tookscan.account.repository.GroupRepository;
import com.tookscan.tookscan.account.repository.mysql.GroupJpaRepository;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupJpaRepository groupJpaRepository;

    @Override
    public Group findByIdOrElseThrow(Long id) {
        return groupJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_GROUP));
    }

    @Override
    public void save(Group group) {
        groupJpaRepository.save(group);
    }

    @Override
    public boolean existsByName(String name) {
        return groupJpaRepository.existsByName(name);
    }

    @Override
    public void deleteByIdOrElseThrow(Long id) {

        Group group = groupJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_GROUP));

        groupJpaRepository.delete(group);
    }

    @Override
    public List<Group> findByIds(List<Long> groupIds) {
        return groupJpaRepository.findGroupByIds(groupIds);
    }

    @Override
    public List<Group> findAll() {
        return groupJpaRepository.findAll();
    }
}
