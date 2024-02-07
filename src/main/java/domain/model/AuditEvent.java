package domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс, представляющий данные об аудите.
 */
@Getter
@Setter
public class AuditEvent {

    private Integer id;
    private String event;
    private User user;
    private LocalDate eventTime;
}
