package domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Класс, представляющий метрику.
 */
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Metric {
    private Integer id;
    private String name;

    /**
     * Конструктор для создания новой метрики с указанным идентификатором и именем.
     *
     * @param name Наименование метрики.
     */
    public Metric(String name) {
        this.name = name;
    }

    public Metric(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
