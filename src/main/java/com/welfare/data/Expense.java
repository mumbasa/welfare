package com.welfare.data;

public class Expense {
private long id;
private String receiver;
private String expense;
private double amount;
private String date;
private String naration;
private String mode;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getReceiver() {
	return receiver;
}
public void setReceiver(String receiver) {
	this.receiver = receiver;
}
public String getExpense() {
	return expense;
}
public void setExpense(String expense) {
	this.expense = expense;
}
public double getAmount() {
	return amount;
}
public void setAmount(double amount) {
	this.amount = amount;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getNaration() {
	return naration;
}
public void setNaration(String naration) {
	this.naration = naration;
}
public String getMode() {
	return mode;
}
public void setMode(String mode) {
	this.mode = mode;
}
}
