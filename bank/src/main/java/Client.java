import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Client {
    private final long id;
    private String name;
    private String history;
    private ArrayList<Count> counts;

    /**
     * Поиск счёта по id
     *
     * @param countId - собственно id для поиска
     * @return - найденный счёт либо null
     */
    public Count getCountById(int countId) {
        Count result = null;
        for (int i = 0; i < this.counts.size(); i++) {
            if (this.counts.get(i).getId() == countId) {
                result = this.counts.get(i);
                break;
            }
        }
        if (result == null) {
            System.out.println("Impossible count");
        }
        return result;
    }

    /**
     * Добавление/убавление суммы на счёт
     *
     * @param countId - id счёта
     * @param amount  - сумма
     */
    public void addAmount(int countId, double amount) {
        Count count = this.getCountById(countId);
        if (count != null && ((amount < 0 && count.getAmount() >= Math.abs(amount)) || amount >= 0)) {
            count.plus(amount);
            this.addHistory(String.format("count №%d was changed on \"%.2f\"", countId, amount));
        } else {
            System.out.println("Impossible operation");
        }
    }

    /**
     * Добавление счёта
     */
    public int setCount() {
        int number = ++Bank.COUNT_ID;
        this.counts.add(new Count(number, 0));
        this.addHistory(String.format("count №%d added", number));
        return number;
    }

    /**
     * Удалеение счёта по id
     *
     * @param countId - id счёта для удаления
     */
    public void deleteCount(int countId) {
        Count count = this.getCountById(countId);
        if (count != null) {
            if (this.counts.size() == 1 && count.getAmount() != 0) {
                System.out.println("Cannot close not null last count!");
            }
            if (this.counts.size() > 1 && count.getAmount() != 0) {
                double sum = count.getAmount();
                this.dropCount(countId);
                this.counts.get(0).plus(sum);
                this.addHistory(String.format("count №%d was deleted; money has transfered to count with id = %d", countId, this.counts.get(0).getId()));
            }
            if (count.getAmount() == 0) {
                this.dropCount(countId);
                this.addHistory(String.format("count №%d was deleted", countId));
            }
        }
    }

    /**
     * Проверка на возможность удалить клиента (все счета должны быть нолевые)
     *
     * @return - можно ли удалить клиента
     */
    public boolean canBeDropped() {
        boolean canBe = true;
        for (int i = 0; i < counts.size(); i++) {
            if (counts.get(i).getAmount() > 0) {
                canBe = false;
                break;
            }
        }
        return canBe;
    }

    /**
     * возвращение суммы на счёте по id
     *
     * @param countId - id счёта
     * @return - сумма
     */
    public double getBalance(int countId) {
        double result = -1;
        Count count = this.getCountById(countId);
        if (count != null) {
            result = count.getAmount();
        }
        return result;
    }

    /**
     * удаление счёта по его id
     *
     * @param countId - id счёта
     */
    private void dropCount(int countId) {
        for (int i = 0; i < counts.size(); i++) {
            if (this.counts.get(i).getId() == countId) {
                counts.remove(i);
                break;
            }
        }
    }

    /**
     * Нахождение максимального id счёта - для последующего создания нового неповторяющегося id
     *
     * @return - максимальный id
     */
    private int findMaxCountId() {
        int result = 0;
        for (int i = 0; i < this.counts.size(); i++) {
            result = Math.max(this.counts.get(i).getId(), result);
        }
        return result;
    }

    /**
     * добавление истории и её оформление в список
     *
     * @param format - исторический "факт"
     */
    private void addHistory(String format) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.history);
        sb.append(System.lineSeparator());
        sb.append(new Date());
        sb.append(": ");
        sb.append(format);
        sb.append("; ");
        this.history = sb.toString();
    }
}
