package com.twilio.trivia.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioConfigService {

    @Value("${ACCOUNTSID}")
    private String accountSid;

    @Value("${AUTHTOKEN}")
    private String authToken;

    @Value("${FROMNUMBER}")
    private String fromNumber;

    public void sendMessage(String toNumber, String messageBody) {
        Twilio.init(accountSid, authToken);
        Message message = Message.creator(new PhoneNumber(toNumber),
                new PhoneNumber(fromNumber), messageBody).create();
        log.info("Message SID: " + message.getSid());
    }
}
