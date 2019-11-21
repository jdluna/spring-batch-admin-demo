package com.example.demo.loadDataToFileStep;

import com.example.demo.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FileProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Map<String, String> DEPT_NAMES = new HashMap<>();

    public FileProcessor() {
        DEPT_NAMES.put("101","Tech");
        DEPT_NAMES.put("102","Tech2");
        DEPT_NAMES.put("103","Tech3");
    }

    @Override
    public Transaction process(Transaction transaction) throws Exception {

        String deptCode = transaction.getDeptName();
        transaction.setDeptName(DEPT_NAMES.get(deptCode));

        return transaction;
    }
}
