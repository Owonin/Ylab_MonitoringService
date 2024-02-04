package domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Класс, представляющий метрику.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Metric {
    private int id;
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric metric = (Metric) o;
        return id == metric.id && name.equals(metric.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
