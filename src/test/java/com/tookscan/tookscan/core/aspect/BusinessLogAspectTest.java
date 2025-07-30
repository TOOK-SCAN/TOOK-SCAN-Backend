package com.tookscan.tookscan.core.aspect;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessLogAspectTest {

    @InjectMocks
    private BusinessLogAspect businessLogAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private BusinessLog businessLog;

    @BeforeEach
    void setUp() {
        LogContext.clear();
    }

    @AfterEach
    void tearDown() {
        LogContext.clear();
    }

    @Test
    void testAround() throws Throwable {
        // given
        when(businessLog.domain()).thenReturn("Test Domain");
        when(businessLog.action()).thenReturn("Test Action");
        when(businessLog.userType()).thenReturn("Test User");
        when(proceedingJoinPoint.proceed()).thenReturn("Test Result");

        // when
        Object result = businessLogAspect.around(proceedingJoinPoint, businessLog);

        // then
        verify(proceedingJoinPoint, times(1)).proceed();
        assert(result.equals("Test Result"));
    }
}
