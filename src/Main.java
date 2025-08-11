import model.Account;
import model.Transaction;
import service.Bank;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final Bank bank = new Bank();

    public static void main(String[] args) {
        seedSampleData();

        // Welcome banner
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "\n============================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE_BOLD_BRIGHT + "      💳 BANK ACCOUNT SIMULATION CLI" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "============================================\n" + ConsoleColors.RESET);

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> createAccount();
                case "2" -> viewAccount();
                case "3" -> deposit();
                case "4" -> withdraw();
                case "5" -> transfer();
                case "6" -> miniStatement();
                case "7" -> listAllAccounts();
                case "8" -> applyInterest();
                case "9" -> exitApp();
                default -> System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "❌ Invalid choice. Please try again." + ConsoleColors.RESET);
            }
        }
    }

    private static void printMenu() {

        //Menu will not be in the output as it was commented for a short output :)
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "\n=========== BANK MENU ===========" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "1. Create Account" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "2. View Account" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "3. Deposit" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "4. Withdraw" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "5. Transfer" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "6. Mini-Statement" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "7. List All Accounts" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "8. Apply Interest" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "9. Exit" + ConsoleColors.RESET);

        System.out.print(ConsoleColors.YELLOW_BOLD + "Choose an option: " + ConsoleColors.RESET);
    }


    //Seeding sample data as its output will become too long else
    private static void seedSampleData() {
        Account a1 = bank.createAccount("Isa Shaikh", 10000, "SAVINGS");
        bank.deposit(a1.getId(), 200.0);
    }

    private static void createAccount() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Owner name: " + ConsoleColors.RESET);
        String name = sc.nextLine();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Initial deposit: " + ConsoleColors.RESET);
        double init = readDouble();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Account type (SAVINGS/CURRENT): " + ConsoleColors.RESET);
        String type = sc.nextLine();
        Account acc = bank.createAccount(name, init, type);
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "✅ Created: " + acc + ConsoleColors.RESET);
    }

    private static void viewAccount() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Account ID: " + ConsoleColors.RESET);
        int id = readInt();
        Optional<Account> a = bank.getAccount(id);
        if (a.isEmpty()) {
            System.out.println(ConsoleColors.RED_BOLD + "❌ Account not found." + ConsoleColors.RESET);
            return;
        }
        Account acc = a.get();
        System.out.println(ConsoleColors.PURPLE_BOLD_BRIGHT + acc + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN_BOLD + "Balance: ₹" + String.format("%.2f", acc.getBalance()) + ConsoleColors.RESET);
    }

    private static void deposit() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Account ID: " + ConsoleColors.RESET);
        int id = readInt();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Amount: " + ConsoleColors.RESET);
        double amt = readDouble();
        boolean ok = bank.deposit(id, amt);
        System.out.println(ok ? ConsoleColors.GREEN_BOLD_BRIGHT + "✅ Deposit successful." + ConsoleColors.RESET :
                ConsoleColors.RED_BOLD_BRIGHT + "❌ Account not found." + ConsoleColors.RESET);
    }

    private static void withdraw() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Account ID: " + ConsoleColors.RESET);
        int id = readInt();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Amount: " + ConsoleColors.RESET);
        double amt = readDouble();
        boolean ok = bank.withdraw(id, amt);
        System.out.println(ok ? ConsoleColors.GREEN_BOLD_BRIGHT + "✅ Withdraw successful." + ConsoleColors.RESET :
                ConsoleColors.RED_BOLD_BRIGHT + "❌ Failed (insufficient funds or account not found)." + ConsoleColors.RESET);
    }

    private static void transfer() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "From Account ID: " + ConsoleColors.RESET);
        int from = readInt();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "To Account ID: " + ConsoleColors.RESET);
        int to = readInt();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Amount: " + ConsoleColors.RESET);
        double amt = readDouble();
        boolean ok = bank.transfer(from, to, amt);
        System.out.println(ok ? ConsoleColors.GREEN_BOLD_BRIGHT + "✅ Transfer successful." + ConsoleColors.RESET :
                ConsoleColors.RED_BOLD_BRIGHT + "❌ Transfer failed (check IDs or funds)." + ConsoleColors.RESET);
    }

    private static void miniStatement() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Account ID: " + ConsoleColors.RESET);
        int id = readInt();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Last N transactions: " + ConsoleColors.RESET);
        int n = readInt();
        List<Transaction> txs = bank.getMiniStatement(id, n);
        if (txs.isEmpty()) {
            System.out.println(ConsoleColors.RED_BOLD + "❌ No transactions or account not found." + ConsoleColors.RESET);
            return;
        }
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "----- Mini Statement -----" + ConsoleColors.RESET);
        int index = 1;
        for (Transaction t : txs) {
            String color = (index % 2 == 0) ? ConsoleColors.CYAN_BRIGHT : ConsoleColors.WHITE_BRIGHT;
            System.out.println(color + index + ". " + t + ConsoleColors.RESET);
            index++;
        }
    }

    private static void listAllAccounts() {
        List<Account> all = bank.listAllAccounts();
        if (all.isEmpty()) {
            System.out.println(ConsoleColors.RED_BOLD + "❌ No accounts yet." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "===== All Accounts =====" + ConsoleColors.RESET);
            all.forEach(acc -> System.out.println(ConsoleColors.CYAN_BOLD + acc + ConsoleColors.RESET));
        }
    }

    private static void applyInterest() {
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "Annual interest rate percent (e.g., 6.0): " + ConsoleColors.RESET);
        double rate = readDouble();
        bank.applyInterestToSavings(rate);
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "✅ Interest applied to SAVINGS accounts." + ConsoleColors.RESET);
    }

    private static void exitApp() {
        System.out.println(ConsoleColors.YELLOW_BOLD + "Thank you for using the Bank CLI. Goodbye!" + ConsoleColors.RESET);
        System.exit(0);
    }

    private static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED_BOLD + "Invalid integer. Using 0." + ConsoleColors.RESET);
            return 0;
        }
    }

    private static double readDouble() {
        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED_BOLD + "Invalid number. Using 0.0" + ConsoleColors.RESET);
            return 0.0;
        }
    }
}


