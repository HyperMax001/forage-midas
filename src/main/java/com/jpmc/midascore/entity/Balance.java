package com.jpmc.midascore.entity;

public class Balance {
    private Float amount;

    public Balance() {
    }

    public Balance(Float amount) {
        this.amount = amount;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Balance{amount=" + amount + "}";
    }
}