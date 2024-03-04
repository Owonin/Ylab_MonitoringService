package com.example.domain.model;

/**
 * Возможные роли пользователей в системе
 */
public enum Role {
    /**
     * Роль администратора
     */
    ADMIN(1),

    /**
     * Роль пользователя
     */
    USER(2);

    private final int id;

    /**
     * Конструктор
     *
     * @param id индификатор роли
     */
    Role(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Получение роли по id
     *
     * @param id    Инфидикатор роли
     * @return      Роль
     */
    public static Role getById(int id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Роль по этому id не найдена: " + id);
    }
}
