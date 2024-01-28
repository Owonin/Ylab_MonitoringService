package in;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, представляющий консольное меню.
 * Позволяет добавлять пункты меню и выполнять действия при выборе каждого пункта.
 */
public class CliMenu {

    /** Список пунктов меню. */
    private final List<MenuItem> items;

    /** Конструктор класса. */
    public CliMenu() {
        items = new ArrayList<>();
    }

    /**
     * Добавляет пункт меню с указанным названием и действием.
     *
     * @param label  Название пункта меню.
     * @param action Действие, которое будет выполняться при выборе данного пункта.
     */
    public void addItem(String label, MenuItemAction action) {
        items.add(new MenuItem(label, action));
    }

    /** Выводит на консоль список пунктов меню. */
    public void show() {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getLabel());
        }
    }

    /** Запускает рендер меню в консоли. */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            show();
            System.out.print("Выберите номер пункта или 'q' для выхода: ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("q")) {
                break;
            }

            try {
                int index = Integer.parseInt(choice) - 1;

                if (index >= 0 && index < items.size()) {
                    items.get(index).executeAction();
                } else {
                    System.out.println("Неверный номер пункта.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите число или 'q'.");
            }
        }
    }
}
