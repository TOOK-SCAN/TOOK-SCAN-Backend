package com.tookscan.tookscan.notice.domain.service;

import com.tookscan.tookscan.notice.domain.Notice;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {
    public Notice createNotice(String title, String content, Boolean isPublic) {
        return Notice.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();
    }

    public void updateNotice(Notice notice, String title, String content, Boolean isPublic) {
        notice.updateTitle(title);
        notice.updateContent(content);
        notice.updateIsPublic(isPublic);
    }

    public void increaseViewCount(Notice notice) {
        notice.increaseViewCount();
    }
}
