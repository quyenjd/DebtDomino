package com.example.debtdomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Balance {
  private double currentBalance;
  private List<Income> incomes;
  private Date currentDate;

  public Balance(double currentBalance, List<Income> incomes, Date currentDate) {
    if (incomes == null) {
      incomes = new ArrayList<>();
    }

    currentDate = currentDate == null ? (new Date()) : currentDate;

    // Bring all incomes' interval to match current date
    for (Income income : incomes) {
      income.jumpToCurrentIntervalOf(currentDate == null ? (new Date()) : currentDate);
    }

    this.currentBalance = currentBalance;
    this.incomes = incomes;
    this.currentDate = currentDate;
  }

  public Income jumpToNextPayment() {
    if (incomes.isEmpty()) {
      return null;
    }

    try {
      Income earlistIncome = incomes.get(0);
      for (Income income : incomes) {
        if (income.getParsedDateOfNextPayment().before(earlistIncome.getParsedDateOfNextPayment())) {
          earlistIncome = income;
        }
      }

      currentBalance += Double.parseDouble(earlistIncome.getAmountOf());
      currentDate = earlistIncome.getParsedDateOfNextPayment();

      earlistIncome.jumpToNextInterval();

      return earlistIncome;
    } catch (Exception e) {
      return null;
    }
  }

  public double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(double newBalance) {
    currentBalance = newBalance;
  }

  public List<Income> getIncomes() {
    return incomes;
  }

  public void setIncomes(List<Income> incomes) {
    this.incomes = incomes;
  }

  public Date getCurrentDate() {
    return currentDate;
  }
}
