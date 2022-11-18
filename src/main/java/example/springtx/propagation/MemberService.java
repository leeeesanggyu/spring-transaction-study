package example.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    /**
     * Member 와 Log 를 각각 저장하는 로직
     */
    @Transactional
    public void joinV1(String userName) {
        Member member = new Member(userName);
        log.info("=== MemberRepository call start ===");
        memberRepository.save(member);
        log.info("=== MemberRepository call end ===");

        Log dbLog = new Log(userName);
        log.info("=== LogRepository call start ===");
        logRepository.save(dbLog);
        log.info("=== LogRepository call end ===");
    }

    /**
     * Member 와 Log 를 각각 저장하는 로직
     * Log 저장 중 예외발생시 복구
     */
    @Transactional
    public void joinV2(String userName) {
        Member member = new Member(userName);
        log.info("=== MemberRepository call start ===");
        memberRepository.save(member);
        log.info("=== MemberRepository call end ===");

        Log dbLog = new Log(userName);
        log.info("=== LogRepository call start ===");
        try {
            logRepository.save(dbLog);
        } catch (RuntimeException e) {
            log.info("Log 저장에 실패했습니다. logMessage = {}", dbLog.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("=== LogRepository call end ===");
    }
}
