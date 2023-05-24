package com.site.ssk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.site.ssk.account.Account;
import com.site.ssk.account.AccountRepository;
import com.site.ssk.meeting.Meeting;
import com.site.ssk.meeting.MeetingRepository;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
 


@SpringBootTest
class SskApplicationTests {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
    private MeetingRepository meetingRepository;
	
	@Transactional
	@Test
	void testJpa() {
		/*
		Optional<Account> oq = this.accountRepository.findById(1);
		Account q = oq.get();
		
		Meeting m1 = new Meeting();
		m1.setData("11111");
		m1.setSummary_data("22222");
		m1.setTitle("33333");
		m1.setCreateDate(LocalDateTime.now());
		m1.setAccount(q);
		this.meetingRepository.save(m1);
		*/
		
	        // Create credentials using a provider chain. For more information, see
	        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
	        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
	        
	        AmazonTranslate translate = AmazonTranslateClient.builder()
	                .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
	                .withRegion("ap-northeast-2")
	                .build();
	 
	        TranslateTextRequest request = new TranslateTextRequest()
	                .withText("Actionable Subsidies are not prohibited ")
	                .withTerminologyNames("PORT")
	                .withSourceLanguageCode("en")
	                .withTargetLanguageCode("ko");
	        TranslateTextResult result  = translate.translateText(request);
	        System.out.println(result.getTranslatedText());
	}
	    
}
