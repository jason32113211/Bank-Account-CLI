package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Basic bank account. Thread-safe on per-account operations.
 * Stores transaction history in memory (append is O(1)).
 */
public class Account {
    private final int id;
    private final String ownerName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();


    private final String accountType; // FOr future Scalability :)

    public Account(int id, String ownerName, double initialDeposit, String accountType) {
        this.id = id;
        this.ownerName = Objects.requireNonNull(ownerName);
        this.balance = initialDeposit;
        this.accountType = accountType == null ? "SAVINGS" : accountType;
        if (initialDeposit > 0) {
            transactions.add(new Transaction(Transaction.Type.DEPOSIT, initialDeposit, "Initial deposit"));
        }
    }

    public int getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public synchronized double getBalance() { return balance; }
    public String getAccountType() { return accountType; }

    // deposit and withdraw are synchronized to avoid race condition :)
    public synchronized void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, amount, "Deposit"));
    }

    /**
     *  withdraw amount: Gives true if successful, false if insufficient funds.
     */
    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive.");
        if (amount > balance) return false; // not enough Funds
        balance -= amount;
        transactions.add(new Transaction(Transaction.Type.WITHDRAW, amount, "Withdraw"));
        return true;
    }


    public synchronized void applyTransferOut(double amount, int toAccountId) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (amount > balance) throw new IllegalStateException("Insufficient funds for transfer.");
        balance -= amount;
        transactions.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, "To account " + toAccountId));
    }

    public synchronized void applyTransferIn(double amount, int fromAccountId) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, "From account " + fromAccountId));
    }


    public synchronized List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    @Override
    public String toString() {
        return String.format("Account[id=%d, owner='%s', type=%s, balance=%.2f]", id, ownerName, accountType, balance);
    }
}
