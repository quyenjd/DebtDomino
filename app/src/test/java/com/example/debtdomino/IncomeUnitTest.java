package com.example.debtdomino;

import org.junit.Test;

import static org.junit.Assert.*;

public class IncomeUnitTest {
    private Income income;

    public IncomeUnitTest() {
        income = new Income(null, "Testing Income", "1000", "Monthly", "2023-01-01", "testing_user");
    }

    @Test
    public void getters() {
        assertEquals(income.getNameOf(), "Testing Income");
        assertEquals(income.getAmountOf(), "1000");
        assertEquals(income.getFrequency(), "Monthly");
        assertEquals(income.getDateOfNextPayment(), "2023-01-01");
        assertEquals(income.getUid(), "testing_user");
    }

    @Test
    public void setters() {
        income.setNameOf("Testing Income 2");
        assertEquals("Testing Income 2", income.getNameOf());

        income.setAmountOf("2000");
        assertEquals("2000", income.getAmountOf());

        income.setFrequency("Fortnightly");
        assertEquals("Fortnightly", income.getFrequency());

        income.setDateOfNextPayment("2023-02-01");
        assertEquals("2023-02-01", income.getDateOfNextPayment());

        income.setUid("testing_user_2");
        assertEquals("testing_user_2", income.getUid());
    }
}