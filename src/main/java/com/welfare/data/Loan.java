package com.welfare.data;

public class Loan {
private long id;
private int loanTypeId; 
private double amount;
private double approvedAmount;
private double arrears;
private String dateApplied;
private String dateApproved;
private String status;
private LoanType type;
private long staffId;
private Staff staff;
private String staffName;
private double shares;
private double year;
private double interestRate;

private double totalToPay;
private String collectionDate;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public double getAmount() {
	return amount;
}
public void setAmount(double amount) {
	this.amount = amount;
}
public String getDateApplied() {
	return dateApplied;
}
public void setDateApplied(String dateApplied) {
	this.dateApplied = dateApplied;
}
public String getDateApproved() {
	return dateApproved;
}
public void setDateApproved(String dateApproved) {
	this.dateApproved = dateApproved;
}
public LoanType getType() {
	return type;
}
public void setType(LoanType type) {
	this.type = type;
}
public long getStaffId() {
	return staffId;
}
public void setStaffId(long staffId) {
	this.staffId = staffId;
}
public Staff getStaff() {
	return staff;
}
public void setStaff(Staff staff) {
	this.staff = staff;
}
public int getLoanTypeId() {
	return loanTypeId;
}
public void setLoanTypeId(int loanTypeId) {
	this.loanTypeId = loanTypeId;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public double getTotalToPay() {
	return totalToPay;
}
public void setTotalToPay(double totalToPay) {
	this.totalToPay = totalToPay;
}
public String getCollectionDate() {
	return collectionDate;
}
public void setCollectionDate(String collectionDate) {
	this.collectionDate = collectionDate;
}
public double getApprovedAmount() {
	return approvedAmount;
}
public void setApprovedAmount(double approvedAmount) {
	this.approvedAmount = approvedAmount;
}
public String getStaffName() {
	return staffName;
}
public void setStaffName(String staffName) {
	this.staffName = staffName;
}
public double getArrears() {
	return arrears;
}
public void setArrears(double arrears) {
	this.arrears = arrears;
}
public double getShares() {
	return shares;
}
public void setShares(double shares) {
	this.shares = shares;
}
public double getYear() {
	return year;
}
public void setYear(double year) {
	this.year = year;
}
public double getInterestRate() {
	return interestRate;
}
public void setInterestRate(double interestRate) {
	this.interestRate = interestRate;
}

	
}
