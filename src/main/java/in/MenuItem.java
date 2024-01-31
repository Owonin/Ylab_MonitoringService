package in;

/**
 * Класс, элемент консольного меню.
 * Содержит данные для отображения и функционирования консольного меню.
 */
public class MenuItem {

    /**
     * Конструктор класса
     *
     * @param label  Название пункта меню.
     * @param action Действие, которое будет выполняться при выборе данного пункта.
     */
    public MenuItem(String label, MenuItemAction action) {
        this.label = label;
        this.action = action;
    }

    /** Название пункта меню. */
    String label;
    /** Действие, которое будет выполняться при выборе данного пункта. */
    private final MenuItemAction action;

    /** Выполняет действие, которое будет при выборе данного пункта. */
    public void executeAction() {
        action.execute();
    }

    /** @return label Название пункта меню. */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
