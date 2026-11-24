
package com.shop.service_shop_management.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String whatsappFrom;

    @Value("${twilio.template.received}")
    private String receivedTemplate;

    @Value("${twilio.template.ready}")
    private String readyTemplate;

    @Value("${twilio.template.delivered}")
    private String deliveredTemplate;

    @PostConstruct
    public void initializeTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void sendReceivedMessage(
            String customerPhone,
            String customerName,
            String billNumber) {

        sendTemplateMessage(
                customerPhone,
                receivedTemplate,
                Map.of(
                        "1", customerName,
                        "2", billNumber
                )
        );
    }

    public void sendReadyMessage(
            String customerPhone,
            String customerName,
            String billNumber) {

        sendTemplateMessage(
                customerPhone,
                readyTemplate,
                Map.of(
                        "1", customerName,
                        "2", billNumber
                )
        );
    }

    public void sendDeliveredMessage(
            String customerPhone,
            String customerName,
            String billNumber) {

        sendTemplateMessage(
                customerPhone,
                deliveredTemplate,
                Map.of(
                        "1", customerName,
                        "2", billNumber
                )
        );
    }

    private void sendTemplateMessage(
            String customerPhone,
            String templateSid,
            Map<String, String> variables) {

        try {

            String formattedNumber = formatPhoneNumber(customerPhone);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedNumber),
                    new PhoneNumber(whatsappFrom),
                    ""
            )
            .setContentSid(templateSid)
            .setContentVariables(
                    new com.google.gson.Gson().toJson(variables)
            )
            .create();

            System.out.println(
                    "WhatsApp message submitted. SID: "
                    + message.getSid()
            );

        } catch (Exception e) {

            System.err.println(
                    "WhatsApp message failed: " + e.getMessage()
            );

        }
    }

    private String formatPhoneNumber(String phone) {

        String cleaned = phone.replaceAll("[^0-9+]", "");

        if (cleaned.startsWith("+")) {
            return cleaned;
        }

        if (cleaned.length() == 10) {
            return "+91" + cleaned;
        }

        if (cleaned.startsWith("91") && cleaned.length() == 12) {
            return "+" + cleaned;
        }

        throw new IllegalArgumentException(
                "Invalid customer phone number: " + phone
        );
    }
}
