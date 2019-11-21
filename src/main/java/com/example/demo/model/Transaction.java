package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transactionRecord")
@Getter @Setter
public class Transaction {

    private String username;
    private int userId;
    private Date transactionDate;
    private double amount;
    private String deptName;

    @Override
    public String toString() {
        return "Transaction{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", amount=" + amount +
                ", deptNumber='" + deptName + '\'' +
                '}';
    }
}
