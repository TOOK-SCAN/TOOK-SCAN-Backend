package com.tookscan.tookscan.core.aspect;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

/**
 * BusinessLog AOP 기능 테스트
 */
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.com.tookscan.tookscan.core.aspect=DEBUG",
    "logging.level.com.tookscan.tookscan.core.aspect.BusinessLogAspectTest=DEBUG"
})
public class BusinessLogAspectTest {

    /**
     * 테스트용 서비스 클래스
     */
    @Service
    public static class TestBusinessService {
        
        @BusinessLog(
            domain = "Test",
            action = "simple operation",
            userType = "Admin"
        )
        public String simpleOperation(String input) {
            return "processed: " + input;
        }
        
        @BusinessLog(
            domain = "Test",
            action = "complex operation",
            userType = "User",
            startDetails = {"input_value: #input", "input_length: #input.length()"},
            endDetails = {"result_length: #result.length()", "processing_time: 'fast'"}
        )
        public String complexOperation(String input) {
            try {
                Thread.sleep(10); // 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Complex result: " + input.toUpperCase();
        }
        
        @BusinessLog(
            domain = "Test",
            action = "operation with object",
            startDetails = {"user_name: #user.name", "user_age: #user.age"},
            endDetails = {"result_user: #testuser.name", "result_age: #testuser.age"}
        )
        public TestUser processUser(TestUser user) {
            return new TestUser(user.name + "_processed", user.age + 1);
        }
    }
    
    /**
     * 테스트용 데이터 클래스
     */
    public static class TestUser {
        public final String name;
        public final int age;
        
        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
    }
    
    @Test
    public void testSimpleLogging() {
        // 이 테스트는 실제로는 로그 출력을 확인하는 것이므로
        // 콘솔에서 로그를 직접 확인해야 합니다.
        System.out.println("=== Testing Simple Logging ===");
        
        TestBusinessService service = new TestBusinessService();
        String result = service.simpleOperation("test input");
        
        System.out.println("Result: " + result);
        System.out.println("Check logs above for AOP logging output");
    }
    
    @Test
    public void testComplexLogging() {
        System.out.println("=== Testing Complex Logging with Details ===");
        
        TestBusinessService service = new TestBusinessService();
        String result = service.complexOperation("hello world");
        
        System.out.println("Result: " + result);
        System.out.println("Check logs above for detailed AOP logging output");
    }
    
    @Test
    public void testObjectLogging() {
        System.out.println("=== Testing Object-based Logging ===");
        
        TestBusinessService service = new TestBusinessService();
        TestUser inputUser = new TestUser("John Doe", 25);
        TestUser result = service.processUser(inputUser);
        
        System.out.println("Input: " + inputUser.name + ", " + inputUser.age);
        System.out.println("Result: " + result.name + ", " + result.age);
        System.out.println("Check logs above for object-based AOP logging output");
    }
}