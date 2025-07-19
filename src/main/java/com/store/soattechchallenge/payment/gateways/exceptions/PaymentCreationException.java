package com.store.soattechchallenge.payment.gateways.exceptions;

  public class PaymentCreationException extends RuntimeException {
  public PaymentCreationException(String message) {
    super(message);
  }
}
