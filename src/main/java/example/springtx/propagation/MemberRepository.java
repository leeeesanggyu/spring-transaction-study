package example.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    @Transactional
    public void save(Member member) {
        log.info("member save");
        em.persist(member);
    }

    public Optional<Member> find(String userName) {
        return em.createQuery("SELECT m FROM Member m WHERE m.userName = :userName", Member.class)
                .setParameter("userName", userName)
                .getResultList().stream().findAny();
    }
}
