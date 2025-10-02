package com.jpmc.midascore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.IncentiveResponse;
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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

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
         UserRecord receiver = userService.getUserById(transaction.getRecipientId());
         logger.info("found both users!");
         if (sender.getBalance() >= transaction.getAmount()) {

             RestTemplate rt = new RestTemplate();

             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_JSON);

             HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

             ResponseEntity<IncentiveResponse> resp = rt.postForEntity(
                     "http://localhost:8080/incentive",
                     request,
                     IncentiveResponse.class
             );

             Float incentives;
             if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                 incentives = resp.getBody().getAmount();
                 logger.info("Incentives = {}", incentives);
             } else {
                 throw new IllegalStateException("Bad response: " + resp.getStatusCode());
             }

//             ObjectMapper mapper = new ObjectMapper();
//             logger.info("Sending to incentive API: {}", mapper.writeValueAsString(transaction));
//             logger.info("Incentive API raw response: {}", resp.getBody());

//             ObjectMapper mapper = new ObjectMapper();
//             try {
//                 logger.info("Sending to incentive API: {}", mapper.writeValueAsString(transaction));
//                 logger.info("Incentive API raw response: {}", mapper.writeValueAsString(resp.getBody()));
//             } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
//                 logger.warn("Failed to serialize for logging. txn={}, respBody={}",
//                         transaction, resp.getBody(), e);
//             }

             sender.setBalance(sender.getBalance() - transaction.getAmount());
             receiver.setBalance(receiver.getBalance() + transaction.getAmount() + incentives);

//             Float lala = 0f;
             transactionRepository.save(
                     new TransactionRecord(sender, receiver, transaction.getAmount(),incentives )
             );
//             logger.info("Transaction completed! Sender: {} has Amount: {} left and Receiver: {} has amount: {}.", sender.getName(), sender.getBalance(), receiver.getName(), receiver.getBalance());
         } else {
             logger.warn("Sender Balance is insufficient");
         }

    }
}