package com.example.debtdomino;

import org.junit.Test;

import static org.junit.Assert.*;

public class DebtUnitTest {
    private Debt debt;

    public DebtUnitTest() {
        debt = new Debt(null, "Testing Debt", "1000", "5", "Monthly", "2023-01-01", "testing_user");
    }

    @Test
    public void getters() {
        assertEquals("Testing Debt", debt.getNameOf());
        assertEquals("1000", debt.getAmountOf());
        assertEquals("5", debt.getRate());
        assertEquals("Monthly", debt.getFrequency());
        assertEquals("2023-01-01", debt.getDateOfNextPayment());
        assertEquals("testing_user", debt.getUid());
    }

    @Test
    public void setters() {
        debt.setNameOf("Testing Debt 2");
        assertEquals("Testing Debt 2", debt.getNameOf());

        debt.setAmountOf("2000");
        assertEquals("2000", debt.getAmountOf());

        debt.setRate("10");
        assertEquals("10", debt.getRate());

        debt.setFrequency("Fortnightly");
        assertEquals("Fortnightly", debt.getFrequency());

        debt.setDateOfNextPayment("2023-02-01");
        assertEquals("2023-02-01", debt.getDateOfNextPayment());

        debt.setUid("testing_user_2");
        assertEquals("testing_user_2", debt.getUid());
    }
}