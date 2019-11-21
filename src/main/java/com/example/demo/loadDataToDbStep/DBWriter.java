package com.example.demo.loadDataToDbStep;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBWriter implements ItemWriter<User> {

    @Autowired
    UserRepository userRepository;

    @Override
    public void write(List<? extends User> users) throws Exception {
        System.out.println("Data Saved: " + users);
//        userRepository.saveAll(users);
    }
}
