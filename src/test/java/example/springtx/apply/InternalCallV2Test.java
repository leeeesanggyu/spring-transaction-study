package example.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    ExternalCallService callService;

    @Test
    void proxyCheck() {
        log.info("aop class = {}", callService.getClass());
        assertThat(AopUtils.isAopProxy(callService)).isTrue();
    }

    @Test
    void internalCall() {
        callService.externalCall();
    }

    @TestConfiguration
    static class CallConfig {
        @Bean
        ExternalCallService externalCallService() {
            return new ExternalCallService(internalCallService());
        }

        @Bean
        InternalCallService internalCallService() {
            return new InternalCallService();
        }
    }

    static class ExternalCallService {

        private final InternalCallService internalCallService;

        ExternalCallService(InternalCallService internalCallService) {
            this.internalCallService = internalCallService;
        }

        public void externalCall() {
            log.info("call external");
            printTxInfo();

            internalCallService.internalCall();
        }

        private void printTxInfo() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("isActive => {}", isActive);
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("isReadOnly => {}", isReadOnly);
        }
    }

    static class InternalCallService {
        @Transactional(readOnly = true)
        public void internalCall() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("isActive => {}", isActive);
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("isReadOnly => {}", isReadOnly);
        }
    }
}