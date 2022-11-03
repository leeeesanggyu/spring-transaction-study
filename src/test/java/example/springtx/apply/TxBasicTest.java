package example.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired
    BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop class = {}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void txActiveTest() {
        boolean tx = basicService.tx();
        assertThat(tx).isTrue();
        boolean noTx = basicService.noTx();
        assertThat(noTx).isFalse();
    }


    @TestConfiguration
    static class TxApplyBasicConfig {
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {
        @Transactional
        public boolean tx() {
            log.info("call tx");
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("isActive => {}", isActive);
            return isActive;
        }

        public boolean noTx() {
            log.info("call no tx");
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("isActive => {}", isActive);
            return isActive;
        }
    }
}
