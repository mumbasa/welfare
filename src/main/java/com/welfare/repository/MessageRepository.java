package com.welfare.repository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import kong.unirest.Unirest;

@Service
public class MessageRepository {

	@Autowired
	Environment env;

	String data;
	

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${mnotify.key}")
	private String smsApiKey;
	
	@Autowired
	JavaMailSender emailSender;
	
	


	public void sendEmail(String messagetext,String email,String subject) {
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
	
		try {
			helper.setText(messagetext,true);
			helper.setTo(email);
			helper.setSubject(subject);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		emailSender.send(message);
		
	}
	

	public String sendSms(String message,String contact) {
	return 	Unirest.get("https://apps.mnotify.net/smsapi").
			queryString("key", smsApiKey).
			queryString("to", contact).
			queryString("msg",message ).
			queryString("sender_id", "iirwelfare")
			.asString().getBody();
	}

	
	
	

}
