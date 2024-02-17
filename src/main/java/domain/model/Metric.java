package domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий метрику.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Metric {
    private Integer id;
    private String name;

    /**
     * Конструктор для создания новой метрики с указанным идентификатором и именем.
     *
     * @param name Наименование метрики.
     */
    public Metric(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
