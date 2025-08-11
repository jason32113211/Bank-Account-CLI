package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT }

    private final Type type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String note; // optional, e.g., "to account 102"

    public Transaction(Type type, double amount, String note) {
        this.type = type;
        this.amount = amount;
        this.note = note == null ? "" : note;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        String ts = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("%s | %s | %.2f | %s", ts, type, amount, note);
    }
}
