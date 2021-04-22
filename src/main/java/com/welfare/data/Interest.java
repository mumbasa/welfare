package com.welfare.data;

public class Interest {
private long id;
private double percent;
private String year;
private String dateApproved;
private String staff;
private double amount;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public double getPercent() {
	return percent;
}
public void setPercent(double percent) {
	this.percent = percent;
}
public String getYear() {
	return year;
}
public void setYear(String year) {
	this.year = year;
}
public String getDateApproved() {
	return dateApproved;
}
public void setDateApproved(String dateApproved) {
	this.dateApproved = dateApproved;
}
public String getStaff() {
	return staff;
}
public void setStaff(String staff) {
	this.staff = staff;
}
public double getAmount() {
	return amount;
}
public void setAmount(double amount) {
	this.amount = amount;
}
}
