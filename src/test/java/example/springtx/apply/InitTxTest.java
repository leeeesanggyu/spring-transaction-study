package example.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    public void txTestV1() {
        boolean txIsActive = hello.initV1();
        // 여기서는 트랜잭션 적용됨
        Assertions.assertThat(txIsActive).isTrue();
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        public Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        /**
         * PostConstruct 는 IOC 에 등록되기전 실행되어 AOP 적용이 불가능합니다.
         */
        @PostConstruct
        @Transactional
        public boolean initV1() {
            boolean isTxActive = TransactionSynchronizationManager.isActualTransactionActive();
            // 여기서는 트랜잭션 적용안됨
            log.info("@PostConstruct isTxActive => {}", isTxActive);
            return isTxActive;
        }

        /**
         *  ApplicationReadyEvent 는 IOC 가 준비가 다 되었을때 이벤트를 받아 호출하기 때문에 AOP 적용이 가능합니다.
         */
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public boolean initV2() {
            boolean isTxActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("@EventListener isTxActive => {}", isTxActive);
            return isTxActive;
        }
    }
}
