package com.example.debtdomino;

import java.util.Date;

public class Payment {
  private Debt debt;
  private double paymentAmount;
  private Date payBefore;

  public Payment(Debt debt, double paymentAmount, Date payBefore) {
    this.debt = debt;
    this.paymentAmount = paymentAmount;
    this.payBefore = payBefore;
  }

  public Debt getDebt() {
    return debt;
  }

  public void setDebt(Debt debt) {
    this.debt = debt;
  }

  public double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Date getPayBefore() {
    return payBefore;
  }

  public void setPayBefore(Date payBefore) {
    this.payBefore = payBefore;
  }
}
