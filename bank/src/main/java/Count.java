import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Count {
    private int id;
    private double amount;

    /**
     * Пополнение (в том числе отрицательное) счёта
     *
     * @param sum - сумма для пополнения
     */
    public void plus(double sum) {
        this.amount += sum;
    }
}
