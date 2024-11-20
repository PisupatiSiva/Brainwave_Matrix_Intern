import java.io.*;
import java.util.*;

public class BMS_ATM {
    static Scanner scanner = new Scanner(System.in);
    static String loggedInUser = null;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- ATM Interface ---");
            System.out.println("1. Login");
            System.out.println("2. New Customer");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    if (login()) {
                        atmMenu();
                    }
                }
                case 2 -> registerCustomer();
                case 3 -> {
                    System.out.println("Thank you for using our ATM. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void registerCustomer() {
        scanner.nextLine();
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Set a 4-digit PIN: ");
        String pin = scanner.next();
        if (pin.length() != 4 || !pin.matches("\\d+")) {
            System.out.println("Invalid PIN! Registration failed.");
            return;
        }
        System.out.print("Enter your 10-digit Mobile Number: ");
        String mobile = scanner.next();
        if (mobile.length() != 10 || !mobile.matches("\\d+")) {
            System.out.println("Invalid Mobile Number! Registration failed.");
            return;
        }

        File folder = new File(".");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        reader.readLine();
                        reader.readLine();
                        String fileMobile = reader.readLine().split(": ")[1];
                        if (fileMobile.equals(mobile)) {
                            System.out.println("Mobile number already registered. Please login.");
                            return;
                        }
                    } catch (IOException e) {
                        System.out.println("Error checking existing mobile numbers: " + e.getMessage());
                    }
                }
            }
        }

        System.out.print("Enter your 11-digit Bank Account Number: ");
        String account = scanner.next();
        if (account.length() != 11 || !account.matches("\\d+")) {
            System.out.println("Invalid Account Number! Registration failed.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(name + ".txt"))) {
            writer.println("Name: " + name);
            writer.println("PIN: " + pin);
            writer.println("Mobile: " + mobile);
            writer.println("Account Number: " + account);
            writer.println("Available Balance: 0.0");
            writer.println();
            writer.println("Transactions:");
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
            return;
        }
        System.out.println("Registration successful! Returning to main menu...");
    }

    static boolean login() {
        System.out.print("Enter your Mobile Number: ");
        String mobileInput = scanner.next();
        System.out.print("Enter your 4-digit PIN: ");
        String pinInput = scanner.next();

        File folder = new File(".");
        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("No user files found! Please register first.");
            return false;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String nameLine = reader.readLine();
                    String pinLine = reader.readLine();
                    String mobileLine = reader.readLine();

                    String fileMobile = mobileLine.split(": ")[1];
                    String filePin = pinLine.split(": ")[1];

                    if (fileMobile.equals(mobileInput) && filePin.equals(pinInput)) {
                        loggedInUser = file.getName().replace(".txt", "");
                        System.out.println("Login successful! Welcome, " + nameLine.split(": ")[1]);
                        return true;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading user file: " + e.getMessage());
                }
            }
        }

        System.out.println("Invalid credentials! Please try again or register if you're a new customer.");
        return false;
    }

    static void atmMenu() {
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n--- ATM Menu ---");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Account Statement");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> depositMoney(loggedInUser);
                case 2 -> withdrawMoney(loggedInUser);
                case 3 -> transferMoney(loggedInUser);
                case 4 -> viewAccountStatement(loggedInUser);
                case 5 -> {
                    System.out.println("Logged out successfully.");
                    exitMenu = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void depositMoney(String username) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        if (amount <= 0) {
            System.out.println("Invalid amount. Transaction failed.");
            return;
        }
        updateBalance(username, amount, "Deposited: " + amount);
        System.out.println("Amount deposited successfully!");
    }

    static void withdrawMoney(String username) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        if (amount <= 0) {
            System.out.println("Invalid amount. Transaction failed.");
            return;
        }
        if (!updateBalance(username, -amount, "Withdrew: " + amount)) {
            System.out.println("Insufficient balance. Transaction failed.");
        } else {
            System.out.println("Amount withdrawn successfully!");
        }
    }

    static void transferMoney(String username) {
        System.out.print("Enter recipient's Account Number: ");
        String recipientAccount = scanner.next();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        if (amount <= 0) {
            System.out.println("Invalid amount. Transaction failed.");
            return;
        }

        File folder = new File(".");
        File[] files = folder.listFiles();
        boolean recipientFound = false;
        String recipientFile = null;

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        reader.readLine();
                        reader.readLine();
                        reader.readLine();
                        String fileAccount = reader.readLine().split(": ")[1];
                        if (fileAccount.equals(recipientAccount)) {
                            recipientFound = true;
                            recipientFile = file.getName();
                            break;
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading user file: " + e.getMessage());
                    }
                }
            }
        }

        if (recipientFound && recipientFile != null) {
            if (updateBalance(username, -amount, "Transferred: " + amount)) {
                updateBalance(recipientFile.replace(".txt", ""), amount, "Received: " + amount);
                System.out.println("Amount transferred successfully!");
            } else {
                System.out.println("Insufficient balance. Transaction failed.");
            }
        } else {
            System.out.println("Recipient account not found. Transaction failed.");
        }
    }

    static void viewAccountStatement(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
            String name = reader.readLine().split(": ")[1];
            String pin = reader.readLine().split(": ")[1];
            String mobile = reader.readLine().split(": ")[1];
            String account = reader.readLine().split(": ")[1];
            String balanceLine = reader.readLine();
            double balance = 0.0;

            if (balanceLine != null && balanceLine.startsWith("Available Balance:")) {
                balance = Double.parseDouble(balanceLine.split(": ")[1]);
            }

            System.out.println("\n--- Account Statement ---");
            System.out.println("Name: " + name);
            System.out.println("Mobile: " + mobile);
            System.out.println("Account Number: " + account);
            System.out.println("Balance: " + balance);

            System.out.println("Last 4 Transactions:");

            reader.readLine();
            String line;
            List<String> transactions = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }

            for (int i = Math.max(0, transactions.size() - 4); i < transactions.size(); i++) {
                System.out.println(transactions.get(i));
            }

        } catch (IOException e) {
            System.out.println("Error reading account statement: " + e.getMessage());
        }
    }

    static boolean updateBalance(String username, double amount, String transaction) {
        File userFile = new File(username + ".txt");
        List<String> userData = new ArrayList<>();
        List<String> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String name = reader.readLine().split(": ")[1];
            String pin = reader.readLine().split(": ")[1];
            String mobile = reader.readLine().split(": ")[1];
            String account = reader.readLine().split(": ")[1];
            double currentBalance = Double.parseDouble(reader.readLine().split(": ")[1]);

            if (currentBalance + amount < 0) {
                return false;
            }

            userData.add("Name: " + name);
            userData.add("PIN: " + pin);
            userData.add("Mobile: " + mobile);
            userData.add("Account Number: " + account);
            userData.add("Available Balance: " + (currentBalance + amount));

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }

            transactions.add(transaction);
        } catch (IOException e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(userFile))) {
            for (String line : userData) {
                writer.println(line);
            }
            writer.println();
            for (String t : transactions) {
                writer.println(t);
            }
        } catch (IOException e) {
            System.out.println("Error saving updated balance: " + e.getMessage());
            return false;
        }

        return true;
    }
}
