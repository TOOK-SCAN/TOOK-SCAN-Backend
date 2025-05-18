package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import java.util.UUID;

@UseCase
public interface UpdateUserOrderCancelUseCase {
    void execute(UUID accountID, Long orderId);
}
