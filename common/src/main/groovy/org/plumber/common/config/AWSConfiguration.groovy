package org.plumber.common.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by jglanz on 11/19/14.
 */
@Configuration
class AWSConfiguration {

	@Value('${aws.access.key:}')
	private String accessKey;

	@Value('${aws.secret.key:}')
	private String secretKey;

	AWSCredentials getCredentials() {
		if(!accessKey || !secretKey)
			return null

		return new BasicAWSCredentials(accessKey, secretKey)
	}

	@Bean
	AmazonS3Client createS3Client() {
		AWSCredentials credentials = getCredentials()

		AmazonS3Client client
		if (credentials == null)
			client = new AmazonS3Client()
		else
			client = new AmazonS3Client(credentials)


		return client
	}

}
