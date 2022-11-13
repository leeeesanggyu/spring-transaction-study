package example.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("tx start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("tx commit start");
        txManager.commit(status);
        log.info("tx commit success");
    }

    @Test
    void rollback() {
        log.info("tx start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("tx rollback start");
        txManager.rollback(status);
        log.info("tx rollback success");
    }

    @Test
    void doubleCommit() {
        log.info("tx1 start");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx1 commit");
        txManager.commit(tx1);

        log.info("tx2 start");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx2 commit");
        txManager.commit(tx2);
    }

    @Test
    void doubleCommitRollback() {
        log.info("tx1 start");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx1 commit");
        txManager.commit(tx1);

        log.info("tx2 start");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("tx2 rollback");
        txManager.rollback(tx2);
    }

    @Test
    void innerCommit() {
        log.info("outer tx start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() => {}", outer.isNewTransaction());

        inner();

        log.info("outer tx commit");
        txManager.commit(outer);
    }

    private void inner() {
        log.info("inner tx start");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() => {}", inner.isNewTransaction());

        log.info("inner tx commit");
        txManager.commit(inner);
    }
}
