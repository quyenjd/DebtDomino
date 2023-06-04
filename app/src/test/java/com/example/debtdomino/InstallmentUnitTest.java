package com.example.debtdomino;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

public class InstallmentUnitTest {
    private Installment installment;

    public InstallmentUnitTest() {
        installment = new Installment(new Date(1672531200000L), 200.0, 10.0);
    }

    @Test
    public void getters() {
        assertEquals(1672531200000L, installment.getDate().getTime());
        assertEquals((Object) 200.0, (Object) installment.getInstallmentAmount());
        assertEquals((Object) 10.0, (Object) installment.getInterest());
    }

    @Test
    public void setters() {
        installment.setDate(new Date(1672617600000L));
        assertEquals(1672617600000L, installment.getDate().getTime());

        installment.setInstallmentAmount(500.0);
        assertEquals((Object) 500.0, (Object) installment.getInstallmentAmount());

        installment.setInterest(7.5);
        assertEquals((Object) 7.5, (Object) installment.getInterest());
    }
}