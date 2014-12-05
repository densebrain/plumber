package org.plumber.worker.tests

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import groovy.util.logging.Slf4j
import org.junit.Test
import org.junit.runner.RunWith

import org.plumber.client.domain.Execution
import org.plumber.client.domain.Job
import org.plumber.client.domain.State
import org.plumber.common.util.JSONUtil
import org.plumber.common.services.JobService
import org.plumber.common.services.JobContext
import org.plumber.common.services.TaskManagerService
import org.plumber.worker.Application
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.web.WebAppConfiguration

/**
 * Created by jglanz on 11/18/14.
 */

@RunWith(SpringTestClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Slf4j
class TaskTests implements TestClassListener {

	String bucketName
	File testFile

	@Autowired
	JobService jobManagerService

	@Autowired
	TaskManagerService taskManagerService

	@Autowired
	AmazonS3Client s3Client

	@Autowired
	ObjectFactory<JobContext> taskContextFactory





	@Override
	void beforeClassSetup() throws Exception {
		bucketName = UUID.randomUUID().toString()

		testFile = new File("/tmp/${UUID.randomUUID().toString()}")
		testFile << 'TestFile'

		File credsFile = new File("${System.getProperty('user.home')}/.aws/credentials")
		assert credsFile.exists(), "You must have a system wide AWS credentials file to test AWS tasks"

		log.info "Creating bucket ${bucketName}"
		s3Client.createBucket(bucketName)
	}

	@Override
	void afterClassSetup() throws Exception {
		testFile.delete()

		log.info "Removing bucket ${bucketName}"

		s3Client.deleteObject(bucketName, 'test-s3-upload');
		s3Client.deleteBucket(bucketName)
	}

	@Test
	void testYouTubeDownload() throws Exception {
		def json = JSONUtil.json(TaskTests.class.getResource("/tasks/YouTubeDownload.json"))
		Job job = new Job()
		job.tasks = taskManagerService.parseTasks([json])
		Execution exec = jobManagerService.execute(new JobContext(), job)

		assert exec.state == State.COMPLETE, exec.results.last?.message
	}


	@Test
	void testS3Upload() throws Exception {
		def json = JSONUtil.json(TaskTests.class.getResource("/tasks/S3Upload.json"))
		//First create the url request
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName,
				"test-s3-upload", HttpMethod.PUT).withContentType(json.args.contentType).
				withExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)));
		json.args.presignedDestinationUrl = s3Client.generatePresignedUrl(request)


		//Create a fake task context
		JobContext context = taskContextFactory.object
		context.values['1'] = [
			result: testFile.absolutePath
		]

		Job job = new Job()
		job.tasks = taskManagerService.parseTasks([json])
		Execution exec = jobManagerService.execute(context, job)

		assert exec.state == State.COMPLETE, exec.results.last?.message
	}


}
