package example.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TxLevelTest {

    @Autowired
    LevelService levelService;

    @Test
    void txLevelTest() {
        boolean write = levelService.write();
        assertThat(write).isFalse();
        boolean read = levelService.read();
        assertThat(read).isTrue();
    }

    @TestConfiguration
    static class TxLevelConfig {
        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }

    @Slf4j
    @Transactional(readOnly = true)
    static class LevelService {

        @Transactional(readOnly = false)
        public boolean write() {
            log.info("call write");
            return getTxReadOnlyInfo();
        }

        public boolean read() {
            log.info("call read");
            return getTxReadOnlyInfo();
        }

        private boolean getTxReadOnlyInfo() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("isReadOnly => {}", isReadOnly);
            return isReadOnly;
        }
    }
}
