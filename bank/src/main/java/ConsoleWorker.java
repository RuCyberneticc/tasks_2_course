
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleWorker {
    private Bank bank;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleWorker(Bank bank) {
        this.bank = bank;
    }

    /**
     * Основной метод, вызывающий соответствующие подметоды, которые активизируют класс Bank
     */
    public void main() {
        boolean isNotAll = true;
        while (isNotAll) {
            System.out.println("Change your option:");
            System.out.println("   1. Add money.");
            System.out.println("   2. Get money.");
            System.out.println("   3. Transfer money from count to count.");
            System.out.println("   4. Open new count.");
            System.out.println("   5. Close count.");
            System.out.println("   6. Get history.");
            System.out.println("   7. Registry new client.");
            System.out.println("   8. Get balance.");
            System.out.println("   9. Get all balances.");
            System.out.println("   0. Delete client.");
            System.out.println("   For exit press 'exit'.");
            String s = scanner.nextLine();
            switch (s) {
                case "1":
                    this.add();
                    break;
                case "2":
                    this.get();
                    break;
                case "3":
                    this.transfer();
                    break;
                case "4":
                    this.openCount();
                    break;
                case "5":
                    this.closeCount();
                    break;
                case "6":
                    this.getHistory();
                    break;
                case "7":
                    this.registry();
                    break;
                case "8":
                    this.getBalance();
                    break;
                case "9":
                    this.getAllBalances();
                    break;
                case "0":
                    this.deleteClient();
                    break;
                case "exit":
                    this.exit();
                    isNotAll = false;
                    break;
                default:
                    System.out.println("Incorrect entry. Try again.");
            }
        }
    }

    private void add() {
        String[] infos = this.getClientInfo("Enter client id: ", "Enter count id: ", "Enter your sum: ");
        this.bank.add(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]), this.preparedDouble(infos[2]));
    }

    private void get() {
        String[] infos = this.getClientInfo("Enter client id: ", "Enter count id: ", "Enter your sum: ");
        this.bank.get(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]), this.preparedDouble(infos[2]));
    }

    private void transfer() {
        String[] infosFrom = this.getClientInfo("Enter payer's id: ", "Enter payer's count id: ", "Enter your sum: ");
        String[] infosTo = this.getClientInfoWithoutSum("Enter recipient id: ", "Enter recipient's count id: ");
        bank.transfer(Integer.parseInt(infosFrom[0]), Integer.parseInt(infosTo[0]), Integer.parseInt(infosFrom[1]), Integer.parseInt(infosTo[1]), this.preparedDouble(infosFrom[2]));
    }

    private void openCount() {
        int clientId = Integer.parseInt(this.getNumbericInfo("Enter client's id", true));
        bank.openCount(clientId);
    }

    private void closeCount() {
        String[] infos = this.getClientInfoWithoutSum("Enter client id", "Enter client's count id");
        bank.closeCount(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]));
    }

    private void getHistory() {
        int clientId = Integer.parseInt(this.getNumbericInfo("Enter client's id", true));
        Client client = bank.getClient(clientId);
        System.out.println(client.getHistory());
    }

    private void registry() {
        String name = this.getStringInfo("Enter new client's name");
        bank.registryClient(name);
    }

    private void getBalance() {
        String[] infos = this.getClientInfoWithoutSum("Enter client id", "Enter client's count id");
        System.out.println("Your balance is " + bank.getBalance(Integer.parseInt(infos[0]), Integer.parseInt(infos[1])));
    }

    private void getAllBalances() {
        ArrayList<Client> clients = bank.getClients();
        for (int i = 0; i < clients.size(); i++) {
            Client temp = clients.get(i);
            System.out.println(String.format("Client %s with id = %d", temp.getName(), temp.getId()));
            ArrayList<Count> counts = temp.getCounts();
            for (int j = 0; j < counts.size(); j++) {
                Count count = counts.get(j);
                System.out.println(String.format("   count with id = %d has amount %f", count.getId(), count.getAmount()));
            }
            System.out.println();
        }
    }

    private void deleteClient() {
        int clientId = Integer.parseInt(this.getNumbericInfo("Enter client's id", true));
        bank.deleteClient(clientId);
    }

    private void exit() {
        System.out.println("Thank you for using this app. Bye!");
        bank.closing();
    }

    /**
     * Метод для запроса информации от клиента "в трёх частях"
     * @param one - запрос первый
     * @param two - запрос второй
     * @param three - запрос третий
     * @return - сформированный в виде массива ответ на все вопросы
     */
    private String[] getClientInfo(String one, String two, String three) {
        String[] infos = new String[3];
        infos[0] = this.getNumbericInfo(one, true);
        infos[1] = this.getNumbericInfo(two, true);
        infos[2] = this.getNumbericInfo(three, false);
        return infos;
    }

    /**
     * Метод для запроса информации от клиента "в двух частях"
     * @param one - запрос первый
     * @param two - запрос второй
     * @return - сформированный в виде массива ответ на все вопросы
     */

    private String[] getClientInfoWithoutSum(String one, String two) {
        String[] infos = new String[2];
        infos[0] = this.getNumbericInfo(one, true);
        infos[1] = this.getNumbericInfo(two, true);
        return infos;
    }

    /**
     * Запрос числовой информации
     * @param info - запрос
     * @param isInteger - является ли число целочисленным или нет (для дальнейшей проверки)
     * @return - ответ пользователя
     */
    private String getNumbericInfo(String info, boolean isInteger) {
        boolean go = false;
        String result = "";
        while (!go) {
            System.out.println(info);
            String forCheck = scanner.nextLine();
            if ((isInteger && this.isCorrectNumber(forCheck)) || (!isInteger && this.isCorrectDoubleNumber(forCheck))) {
                result = forCheck;
                go = true;
            } else {
                System.out.println("Incorrect entry!");
            }
        }
        return result;
    }

    /**
     * Проверка данных, что введённый результат действительно является дробный числом
     * @param forCheck - проверяемый результат
     * @return - дробное число или нет
     */
    private boolean isCorrectDoubleNumber(String forCheck) {
        boolean isCorrect = true;
        try {
            Double.parseDouble(forCheck);
        } catch (NumberFormatException nfe) {
            isCorrect = false;
        }
        return isCorrect;
    }

    /**
     * Проверка, что вводимая информация действительно является именем (т.е. состоит только из букв)
     * @param info - проверяемая информация
     * @return - имя или нет
     */
    private String getStringInfo(String info) {
        boolean go = false;
        String result = "";
        while (!go) {
            System.out.println(info);
            String forCheck = scanner.nextLine();
            if (info.length() > 0 && isCorrectString(forCheck)) {
                result = forCheck;
                go = true;
            } else {
                System.out.println("Incorrect entry! Don't use numbers, spaces and symbols in name!");
            }
        }
        return result;
    }

    /**
     * Проверка, что запрашивая информация - действительно число (через REGEX)
     * @param forCheck - информация для проверки
     * @return - число или нет
     */
    private boolean isCorrectNumber(String forCheck) {
        return forCheck.matches("\\d+");
    }

    /**
     * Проверка, что запрашиваемая информация - действительно строка
     * @param forCheck - строка для проверки
     * @return - строка или нет
     */
    private boolean isCorrectString(String forCheck) {
        boolean isSymbolic = true;
        for (int i = 0; i < forCheck.length(); i++) {
            if (!Character.isAlphabetic(forCheck.charAt(i))) {
                isSymbolic = false;
                break;
            }
        }
        return isSymbolic;
    }

    /**
     * Подготовка двуззначного после точки Double
     * @param doub - строка для подготовки
     * @return - подготовленный Double
     */
    private double preparedDouble(String doub) {
        doub += "00";
        double result = -1;
        int pos = doub.lastIndexOf('.');
        if (pos != -1) {
            result = Double.parseDouble(doub.substring(0, pos + 3));
        } else {
            result = Double.parseDouble(doub);
        }
        return result;
    }
}
