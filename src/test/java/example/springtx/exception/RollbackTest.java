package example.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> rollbackService.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void checkedExceptionRollbackFor() {
        Assertions.assertThatThrownBy(() -> rollbackService.checkedExceptionRollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {
        // Runtime Exception : rollback
        @Transactional
        public void runtimeException() {
            log.info("Call RuntimeException");
            throw new RuntimeException();
        }

        // Checked Exception : commit
        @Transactional
        public void checkedException() throws MyException {
            log.info("Call CheckedException");
            throw new MyException();
        }

        // Checked Exception 을 rollbackFor 지정 : rollback
        @Transactional(rollbackFor = MyException.class)
        public void checkedExceptionRollbackFor() throws MyException {
            log.info("Call CheckedException (rollbackFor)");
            throw new MyException();
        }
    }

    static class MyException extends Exception {}
}
