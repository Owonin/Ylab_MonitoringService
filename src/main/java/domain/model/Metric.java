package domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий метрику.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Metric {

    /**
     * Конструктор для создания новой метрики с указанным идентификатором и именем.
     *
     * @param id   Идентификатор метрики.
     * @param name Наименование метрики.
     */
    public Metric(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private String id;
    private String name;

}
