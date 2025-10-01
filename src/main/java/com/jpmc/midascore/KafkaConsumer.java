package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${general.kafka-group}")
    @Transactional
    public void listen(Transaction transaction) {
         UserRecord sender = userService.getUserById(transaction.getSenderId());
         logger.info("ZINDAGI JHANDWA FIR BHI GHAMANDWA!");
         UserRecord receiver = userService.getUserById(transaction.getRecipientId());
         if (sender.getBalance() >= transaction.getAmount()) {
             sender.setBalance(sender.getBalance() - transaction.getAmount());
             receiver.setBalance(receiver.getBalance() + transaction.getAmount());
             userRepository.save(sender);
             userRepository.save(receiver);
             transactionRepository.save(
                     new TransactionRecord(sender, receiver, transaction.getAmount())
             );
             logger.info("Transaction completed! Sender: {} has Amount: {} left and Receiver: {} has amount: {}.", sender.getName(), sender.getBalance(), receiver.getName(), receiver.getBalance());
         } else {
             logger.warn("Sender Balance is insufficient");
         }

    }
}