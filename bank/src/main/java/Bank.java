import java.util.ArrayList;

public class Bank {
    private Connector connector;
    private static boolean created = false;
    private ArrayList<Client> clients;
    public static int COUNT_ID = 0;

    private Bank() {
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public Client getClient(int clientId) {
        Client result = null;
        for (int i = 0; i < this.clients.size(); i++) {
            if (this.clients.get(i).getId() == clientId) {
                result = this.clients.get(i);
                break;
            }
        }
        if (result == null) {
            System.out.println("Impossible client");
        }
        return result;
    }

    /**
     * Использован паттерн "Singleton"
     *
     * @return - экземпляр банка
     */
    public static Bank getInstance() {
        Bank bank = null;
        if (!created) {
            bank = new Bank();
            bank.connector = new Connector();
            bank.clients = bank.connector.getAllClients();
            for (int i = 0; i < bank.clients.size(); i++) {
                bank.connector.putAllCounts(bank.clients.get(i));
            }
        }
        return bank;
    }

    /**
     * Добавление суммы (в т.ч. отрицательной) на счёт клиента
     *
     * @param clientId - id клиента
     * @param countId  - id счёта
     * @param amount   - сумма
     */
    public void add(int clientId, int countId, double amount) {
        Client client = this.getClient(clientId);
        if (client != null) {
            client.addAmount(countId, amount);
            connector.add(countId, client.getCountById(countId).getAmount(), client.getHistory(), clientId);
        }
    }

    /**
     * Снятие (отрицательное добавление) со счёта клиента
     *
     * @param clientId - id клиента
     * @param countId  - id счёта
     * @param amount   - сумма
     */
    public void get(int clientId, int countId, double amount) {
        this.add(clientId, countId, amount * -1);
    }

    /**
     * Перевод со счёта на счёт
     *
     * @param fromClientId - id клиента-отправителя
     * @param toClientId   - id клиента-получателя
     * @param fromCountId  - id счёта для списания
     * @param toCountId    - id счёта для пополнения
     * @param amount       - сумма
     */
    public void transfer(int fromClientId, int toClientId, int fromCountId, int toCountId, double amount) {
        if (this.getClient(fromClientId) != null && this.getClient(toClientId) != null &&
                this.getClient(fromClientId).getCountById(fromCountId) != null &&
                this.getClient(toClientId).getCountById(toCountId) != null) {
            this.get(fromClientId, fromCountId, amount);
            this.add(toClientId, toCountId, amount);
        } else {
            System.out.println("Impossible client(s)");
        }
    }

    /**
     * Открытие счёта
     *
     * @param clientId - id клиента, на которого открываем счёт
     */
    public void openCount(int clientId) {
        Client client = this.getClient(clientId);
        if (client != null) {
            int id = client.setCount();
            connector.openCount(clientId, id, client.getHistory());
        }
    }

    /**
     * Закрытие счёта
     *
     * @param clientId - id клиента для закрытия счёта
     * @param countId  - id счёта для закрытия
     */
    public void closeCount(int clientId, int countId) {
        Client client = this.getClient(clientId);
        if (client != null) {
            client.deleteCount(countId);
            connector.deleteCount(countId, clientId, client.getHistory());
        }
    }

    /**
     * Регистрация нового клиента
     *
     * @param name - имя клиента
     */
    public void registryClient(String name) {
        this.clients.add(new Client(this.findFirstEmptyId(), name, "", new ArrayList<Count>()));
        connector.registryClient(name);
    }

    /**
     * удаление клиента по id (если у него все счета нолевые)
     *
     * @param clientId - id клиента для удаления
     */
    public void deleteClient(int clientId) {
        for (int i = 0; i < this.clients.size(); i++) {
            if (this.clients.get(i).getId() == clientId) {
                if (this.clients.get(i).canBeDropped()) {
                    this.clients.remove(i);
                    connector.deleteClient(clientId);
                }
            }
        }

    }

    /**
     * Получение баланса по id клиента и счёта
     *
     * @param clientId - id клиента
     * @param countId  - id счёта
     * @return - сумма на счёте
     */
    public double getBalance(int clientId, int countId) {
        double result = -1;
        Client client = this.getClient(clientId);
        if (client != null) {
            result = client.getBalance(countId);
        }
        return result;
    }

    /**
     * Полная банковская выписка
     *
     * @return - выписка по всему банку
     */
    public String getAllInfo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.clients.size(); i++) {
            Client temp = this.clients.get(i);
            sb.append(String.format("Client %s (id %d):%s", temp.getName(), temp.getId(), System.lineSeparator()));
            for (int j = 0; j < temp.getCounts().size(); j++) {
                Count tempC = temp.getCounts().get(j);
                sb.append(String.format("--- count №%d (id %d): amount = %s;%s", j + 1, tempC.getId(), tempC.getAmount(), System.lineSeparator()));
            }
        }
        return sb.toString();
    }

    /**
     * Закрытие банка и сохранение всей информации в БД
     */
    public void closing() {
        this.connector = new Connector();
        connector.writeAndClose(clients);
    }


    /**
     * Поиск минимального id для клиента
     *
     * @return - минимальный незанятый id
     */
    private int findFirstEmptyId() {
        int result = 1;
        boolean hasNoId = true;
        while (hasNoId) {
            hasNoId = false;
            for (int i = 0; i < this.clients.size(); i++) {
                if (clients.get(i).getId() == result) {
                    hasNoId = true;
                    break;
                }
            }
        }
        return result;
    }
}
