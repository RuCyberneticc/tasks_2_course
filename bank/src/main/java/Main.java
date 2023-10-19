// перед началом работы следует создать БД в PgAdmin. Для этого используется следующий код:

// CREATE DATABASE bank;
// CREATE TABLE IF NOT EXISTS client (id SERIAL PRIMARY KEY, name VARCHAR(100), history TEXT);
// CREATE TABLE IF NOT EXISTS count (id SERIAL PRIMARY KEY, count_holder INTEGER, amount DOUBLE PRECISION, FOREIGN KEY (count_holder) REFERENCES client(id));

// также можно создать двух тестовых пользователей и три тестовых счёта
// INSERT INTO client(name) VALUES ('First');
// INSERT INTO client(name) VALUES ('Second');
// INSERT INTO count(count_holder, amount) VALUES(1, 100);
// INSERT INTO count(count_holder, amount) VALUES(1, 200);
// INSERT INTO count(count_holder, amount) VALUES(2, 2000);


public class Main {
    public static void main(String[] args) {
        //Bank bank = Bank.getInstance();

        // ... здесь может быть любая работа с банком

        // при создании тестовой БД будет выведена информация о пользователях и их счетах
        //System.out.println(bank.getAllInfo());
        // при создании тестовой БД будет сделана трансференция, которая сохранится при закрытии банка
        //bank.transfer(2, 1, 3, 1, 200);
        //bank.closing();
        ConsoleWorker cw = new ConsoleWorker(Bank.getInstance());
        cw.main();
    }
}
