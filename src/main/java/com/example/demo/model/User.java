package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;


@Entity
@Getter
@Setter
public class User {

    @Id
    private Integer id;
    private String name;
    private String dept;
    private BigDecimal salary;

    public User() {
    }

    public User(Integer id, String name, String dept, BigDecimal salary) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", salary=" + salary +
                '}';
    }
}
