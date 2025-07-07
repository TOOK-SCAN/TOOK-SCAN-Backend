package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.notice.application.usecase.CreateAdminNoticeUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.service.NoticeService;
import com.tookscan.tookscan.notice.presentation.dto.request.CreateAdminNoticeRequestDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminNoticeService implements CreateAdminNoticeUseCase {

    private final NoticeRepository noticeRepository;

    private final NoticeService noticeService;

    @Override
    @Transactional
    public void execute(CreateAdminNoticeRequestDto requestDto) {
        Notice notice = noticeService.createNotice(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getIsPublic()
        );
        noticeRepository.save(notice);
    }
} 