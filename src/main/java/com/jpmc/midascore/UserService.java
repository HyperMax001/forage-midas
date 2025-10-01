package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public UserRecord getUserById(long id) {
        UserRecord user = userRepository.findById(id);
        if (user != null) {
            logger.info("Found user: {} with balance: {}", user.getName(), user.getBalance());
        } else {
            logger.warn("User with ID {} not found", id);
        }
        return user;
    }

//    public UserRecord updateUserBalance(long id, float newBalance) {
//        UserRecord user = userRepository.findById(id);
//        if (user != null) {
//            float oldBalance = user.getBalance();
//            user.setBalance(newBalance);
//            userRepository.save(user);
//            logger.info("Updated user {}: balance {} -> {}", user.getName(), oldBalance, newBalance);
//            return user;
//        } else {
//            logger.warn("Cannot update: User with ID {} not found", id);
//            return null;
//        }
//    }

    public List<UserRecord> getAllUsers() {
        List<UserRecord> users = (List<UserRecord>) userRepository.findAll();
        logger.info("Found {} users in database", users.size());
        return users;
    }
}
