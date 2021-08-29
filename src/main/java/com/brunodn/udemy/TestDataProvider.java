package com.brunodn.udemy;

import com.brunodn.udemy.auth.persistence.UserEntity;
import com.brunodn.udemy.auth.persistence.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

@Singleton
public class TestDataProvider {

    final UserRepository users;


    public TestDataProvider(UserRepository users) {
        this.users = users;
    }

    @EventListener
    public void init(StartupEvent event){
        final String email = "brunodn@gmail.com";

        if(users.findByEmail(email).isEmpty()){
            final UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setPassword("secret");
            users.save(user);
        }

    }
}
