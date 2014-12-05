package org.plumber.common.services.tasks

import groovy.util.logging.Slf4j
import org.codehaus.groovy.runtime.InvokerHelper
import org.plumber.client.domain.*
import org.plumber.client.domain.OSType
import org.plumber.client.domain.Requirements
import org.plumber.client.domain.TaskExecutor
import org.plumber.client.domain.TaskResult
import org.plumber.client.domain.TaskType
import org.plumber.common.services.FileService
import org.plumber.common.services.ShellCommand

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/18/14.
 */

@Slf4j
@Service
class S3UploadTaskType implements TaskType {

	@Autowired
	ShellCommand shell

	@Autowired
	FileService files

	Requirements requirements() {
		[
		        osTypes: [OSType.ALL],
				packages: []
		]
	}

	@Override
	String name() {
		return "S3Upload"
	}

	@Override
	TaskExecutor executor() {
		return new S3UploadTaskExecutor()
	}



	class S3UploadTaskExecutor implements TaskExecutor {

		String fileUrl, presignedDestinationUrl, contentType = "application/octet"

		@Override
		TaskResult execute(JobContext context, Task task) throws Exception {

			//Set the arguments
			InvokerHelper.setProperties(this, task.args)

			try {
				String filePath = context.resolveObject(JobContext.createURL(fileUrl), String.class)

				context.log("Uploading ${filePath} to ${presignedDestinationUrl}")

				URL uploadUrl = new URL(presignedDestinationUrl)
				HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection()
				FileInputStream fis = null
				OutputStream os = null
				try {
					connection.setDoOutput(true)
					connection.setRequestMethod("PUT")
					connection.setRequestProperty("Content-Type", contentType)
					os = connection.getOutputStream()

					fis = new FileInputStream(filePath)

					int read
					byte[] buf = new byte[2048]
					while ((read = fis.read(buf)) != -1) {
						os.write(buf,0,read)
					}
					os.close()

					int responseCode = connection.getResponseCode()
					context.log("Service returned response code " + responseCode)

					return new TaskResult(
							code: TaskResult.Code.SUCCESS
					)
				} finally {
					files.close(fis)
					files.close(os)
					connection.disconnect()
				}
			} catch (e) {
				context.log("Failed to upload to S3", e)
				return new TaskResult(
						code: TaskResult.Code.FAILURE,
						message: e.message
				)
			}

		}
	}
}
