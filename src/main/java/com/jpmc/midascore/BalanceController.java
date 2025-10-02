package com.jpmc.midascore;

import com.jpmc.midascore.entity.Balance;
import com.jpmc.midascore.entity.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        try {
            UserRecord user = userService.getUserById(userId);
            logger.info("Retrieved balance for user {}: {}", userId, user.getBalance());
            return new Balance(user.getBalance());
        } catch (Exception e) {
            logger.warn("User {} not found, returning balance of 0", userId);
            return new Balance(0f);
        }
    }
}