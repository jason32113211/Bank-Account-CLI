package service;

import model.Account;
import model.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bank service: manages accounts and operations.
 * Uses ConcurrentHashMap for thread-safe O(1) account lookup.
 */
public class Bank {
    // For O(1) average-case get/put/remove for accounts by id only
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

    // Id generator which Starts generation from 1000 which is preseeded so next will me 1001 :)
    private final AtomicIntegerWrapper idGen = new AtomicIntegerWrapper(1000);

    // Create account with initial deposit
    public Account createAccount(String ownerName, double initialDeposit, String accountType) {
        int id = idGen.next();
        Account acc = new Account(id, ownerName, initialDeposit, accountType);
        accounts.put(id, acc);
        return acc;
    }

    public Optional<Account> getAccount(int accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    // Deposit
    public boolean deposit(int accountId, double amount) {
        Account acc = accounts.get(accountId);
        if (acc == null) return false;
        acc.deposit(amount);
        return true;
    }

    // Withdraw
    public boolean withdraw(int accountId, double amount) {
        Account acc = accounts.get(accountId);
        if (acc == null) return false;
        return acc.withdraw(amount);
    }

    // Transfer (two account lookups) - does minimal locking: synchronizes on both accounts in consistent order to avoid deadlock
    public boolean transfer(int fromId, int toId, double amount) {
        if (fromId == toId) return false;
        Account aFrom = accounts.get(fromId);
        Account aTo = accounts.get(toId);
        if (aFrom == null || aTo == null) return false;

        // lock order by account id to avoid deadlock
        Account first = fromId < toId ? aFrom : aTo;
        Account second = fromId < toId ? aTo : aFrom;

        synchronized (first) {
            synchronized (second) {
                if (aFrom.getBalance() < amount) return false;
                aFrom.applyTransferOut(amount, toId);
                aTo.applyTransferIn(amount, fromId);
                return true;
            }
        }
    }

    public List<Account> listAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Transaction> getMiniStatement(int accountId, int lastN) {
        Account acc = accounts.get(accountId);
        if (acc == null) return Collections.emptyList();
        List<Transaction> all = acc.getTransactionHistory();
        if (lastN <= 0) return Collections.emptyList();
        int from = Math.max(0, all.size() - lastN);
        return all.subList(from, all.size());
    }

    // Apply simple interest to all savings accounts (demonstrates future extension)
    public void applyInterestToSavings(double annualRatePercent) {
        // O(number_of_accounts) - operationally acceptable for batch job
        for (Account acc : accounts.values()) {
            // for scalability, we could filter by account type and compute monthly interest
            if ("SAVINGS".equalsIgnoreCase(acc.getAccountType())) {
                synchronized (acc) {
                    double monthly = acc.getBalance() * (annualRatePercent / 100) / 12.0;
                    if (monthly > 0) {
                        acc.deposit(monthly); // this will add a transaction
                    }
                }
            }
        }
    }
}

/**
 * Tiny wrapper to keep id generator simple and thread-safe.
 */
class AtomicIntegerWrapper {
    private int value;
    public AtomicIntegerWrapper(int start) { this.value = start; }
    public synchronized int next() { return ++value; }
}
