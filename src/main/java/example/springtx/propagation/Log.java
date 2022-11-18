package example.springtx.propagation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Log {

    public Log() {

    }

    public Log(String message) {
        this.message = message;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String message;
}
