package example.springtx.propagation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    private static String userName = "이상규";
    private static String exceptionName = "로그예외";

    /**
     * MemberService        tx : OFF
     * MemberRepository     tx : ON
     * LogRepository        tx : ON
     */
    @Test
    void outerTxOff_success() {
        memberService.joinV1(userName);

        Assertions.assertThat(memberRepository.find(userName)).isPresent();
        Assertions.assertThat(logRepository.find(userName)).isPresent();
    }

    /**
     * MemberService        tx : OFF
     * MemberRepository     tx : ON
     * LogRepository        tx : ON
     */
    @Test
    void outerTxOff_failed() {
        Assertions.assertThatThrownBy(() -> memberService.joinV1(exceptionName))
                        .isInstanceOf(RuntimeException.class);

        Assertions.assertThat(memberRepository.find(exceptionName)).isPresent();
        Assertions.assertThat(logRepository.find(exceptionName)).isEmpty();
    }

    /**
     * MemberService        tx : ON
     * MemberRepository     tx : OFF
     * LogRepository        tx : OFF (Exception)
     */
    @Test
    void outerTxOn_failed() {
        Assertions.assertThatThrownBy(() -> memberService.joinV1(exceptionName))
                .isInstanceOf(RuntimeException.class);

        Assertions.assertThat(memberRepository.find(exceptionName)).isEmpty();
        Assertions.assertThat(logRepository.find(exceptionName)).isEmpty();
    }

    /**
     * MemberService        tx : ON
     * MemberRepository     tx : ON
     * LogRepository        tx : ON (Exception)
     *
     * MemberService 에서 catch 하여 정상흐를으로 만들어도
     * 이미 물리 트랜잭션에 rollbackOnly = true 로 적용되어서 UnexpectedRollbackException 이 뜨고
     * MemberRepository 도 rollback이 됩니다.
     */
    @Test
    void exception_recovery_failed() {
        Assertions.assertThatThrownBy(() -> memberService.joinV2(exceptionName))
                        .isInstanceOf(UnexpectedRollbackException.class);

        Assertions.assertThat(memberRepository.find(exceptionName)).isEmpty();
        Assertions.assertThat(logRepository.find(exceptionName)).isEmpty();
    }

    /**
     * MemberService        tx : ON
     * MemberRepository     tx : ON
     * LogRepository        tx (REQUIRE_NEW) : ON (Exception)
     */
    @Test
    void exception_recovery_success() {
        memberService.joinV2(exceptionName);

        Assertions.assertThat(memberRepository.find(exceptionName)).isPresent();
        Assertions.assertThat(logRepository.find(exceptionName)).isEmpty();
    }
}