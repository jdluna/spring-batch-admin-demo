package com.example.demo.loadDataToDbStep;

import com.example.demo.model.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DBProcessor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAMES = new HashMap<>();

    public DBProcessor() {
        DEPT_NAMES.put("101","Tech");
        DEPT_NAMES.put("102","Tech2");
        DEPT_NAMES.put("103","Tech3");
    }

    @Override
    public User process(User user) throws Exception {

        String deptCode = user.getDept();
        user.setDept(DEPT_NAMES.get(deptCode));

        return user;
    }
}
