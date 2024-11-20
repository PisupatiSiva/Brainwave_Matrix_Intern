import java.io.*;
import java.util.*;

public class BMS_ATM {
    static Scanner scanner = new Scanner(System.in);
    static String loggedInUser = null; // Track the logged-in user

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
                        atmMenu(); // Show ATM menu after successful login
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
        scanner.nextLine(); // Clear the buffer
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine(); // Allows spaces in the name
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

        // Check if the mobile number is already registered
        File folder = new File(".");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        reader.readLine(); // Skip name
                        reader.readLine(); // Skip pin
                        String fileMobile = reader.readLine().split(": ")[1];
                        if (fileMobile.equals(mobile)) {
                            System.out.println("Mobile number already registered. Please login.");
                            return; // Exit the registration process
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

        // Save details to file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(name + ".txt"))) {
            writer.println("Name: " + name);
            writer.println("PIN: " + pin);
            writer.println("Mobile: " + mobile);
            writer.println("Account Number: " + account);
            writer.println("Available Balance: 0.0");
            writer.println(); // Add a blank line
            writer.println("Transactions:"); // Placeholder for transactions
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
                    String nameLine = reader.readLine(); // Read Name
                    String pinLine = reader.readLine();  // Read PIN
                    String mobileLine = reader.readLine(); // Read Mobile

                    String fileMobile = mobileLine.split(": ")[1]; // Extract mobile
                    String filePin = pinLine.split(": ")[1]; // Extract PIN

                    if (fileMobile.equals(mobileInput) && filePin.equals(pinInput)) {
                        loggedInUser = file.getName().replace(".txt", ""); // Set logged-in user
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
        System.out.print("Enter amount to deposit (multiples of 100 only): ");
    double amount = scanner.nextDouble();
    
    // Check if the amount is a multiple of 100
    if (amount % 100 != 0) {
        System.out.println("Invalid amount. Only multiples of 100 are allowed.");
        return;
    }
    
    if (amount <= 0) {
        System.out.println("Invalid amount. Transaction failed.");
        return;
    }
    
    updateBalance(username, amount, "Deposited: " + amount);
    System.out.println("Amount deposited successfully!");
    }


    static void withdrawMoney(String username) {
          System.out.print("Enter amount to withdraw (multiples of 100 only): ");
    double amount = scanner.nextDouble();
    
    // Check if the amount is a multiple of 100
    if (amount % 100 != 0) {
        System.out.println("Invalid amount. Only multiples of 100 are allowed.");
        return;
    }
    
    if (amount <= 0) {
        System.out.println("Invalid amount. Transaction failed.");
        return;
    }
    
    if (!updateBalance(username, -amount, "Withdraw: " + amount)) {
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

    // Search for the recipient's account and display confirmation
    File folder = new File(".");
    File[] files = folder.listFiles();
    boolean recipientFound = false;
    String recipientFile = null;
    String recipientName = null;

    if (files != null) {
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String nameLine = reader.readLine(); // Read name
                    reader.readLine(); // Skip pin
                    reader.readLine(); // Skip mobile
                    String fileAccount = reader.readLine().split(": ")[1]; // Read account number
                    
                    // Check if account matches
                    if (fileAccount.equals(recipientAccount)) {
                        recipientFound = true;
                        recipientFile = file.getName();
                        recipientName = nameLine.split(": ")[1]; // Extract recipient's name
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading user file: " + e.getMessage());
                }
            }
        }
    }

    if (recipientFound && recipientName != null) {
        // Display recipient details and ask for confirmation
        System.out.println("\n--- Recipient Details ---");
        System.out.println("Name: " + recipientName);
        System.out.println("Account Number: " + recipientAccount);
        System.out.print("Do you want to confirm this transfer? (yes/no): ");
        String confirmation = scanner.next();

        if (confirmation.equalsIgnoreCase("yes")) {
            if (updateBalance(username, -amount, "Transferred: " + amount)) {
                updateBalance(recipientFile.replace(".txt", ""), amount, "Received: " + amount);
                System.out.println("Amount transferred successfully!");
            } else {
                System.out.println("Insufficient balance. Transaction failed.");
            }
        } else {
            System.out.println("Transfer cancelled.");
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
        String balanceLine = reader.readLine(); // Line with balance information
        double balance = 0.0;
        
        // Extract balance from the line
        if (balanceLine != null && balanceLine.startsWith("Available Balance:")) {
            balance = Double.parseDouble(balanceLine.split(": ")[1]);
        }

        System.out.println("\n--- Account Statement ---");
        System.out.println("Name: " + name);
        System.out.println("Mobile: " + mobile);
        System.out.println("Account Number: " + account);
        System.out.println("Balance: " + balance);

        System.out.println("Last 4 Transactions:");

        // Skip the 'Transactions:' line
        reader.readLine();
        String line;
        List<String> transactions = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            transactions.add(line);
        }

        // Print the last 4 transactions
        for (int i = Math.max(1, transactions.size() - 4); i < transactions.size(); i++) {
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

            // Check if sufficient funds are available
            if (currentBalance + amount < 0) {
                return false; // Insufficient funds
            }

            // Update user data
            userData.add("Name: " + name);
            userData.add("PIN: " + pin);
            userData.add("Mobile: " + mobile);
            userData.add("Account Number: " + account);
            userData.add("Available Balance: " + (currentBalance + amount));

            reader.readLine(); // Skip "Transactions:"
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }

            // Add new transaction
            transactions.add(transaction);
        } catch (IOException e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return false;
        }

        // Write updated information back to file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(userFile))) {
            for (String line : userData) {
                writer.println(line);
            }
            writer.println();
            writer.println("Transactions:");
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
