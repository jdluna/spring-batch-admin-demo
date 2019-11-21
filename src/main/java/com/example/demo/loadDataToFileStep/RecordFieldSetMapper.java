package com.example.demo.loadDataToFileStep;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.demo.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class RecordFieldSetMapper implements FieldSetMapper<Transaction> {

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Transaction transaction = new Transaction();

        transaction.setUsername(fieldSet.readString("userName"));
        transaction.setUserId(fieldSet.readInt(1));
        transaction.setAmount(fieldSet.readDouble(3));
        transaction.setDeptName(fieldSet.readString("deptName"));
        String dateString = fieldSet.readString(2);
        try {
            transaction.setTransactionDate(dateFormat.parse(dateString));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return transaction;
    }
}
