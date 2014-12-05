package org.plumber.common.services.tasks

import groovy.util.logging.Slf4j
import org.codehaus.groovy.runtime.InvokerHelper
import org.plumber.client.domain.JobContext
import org.plumber.client.domain.OSType
import org.plumber.client.domain.Requirements
import org.plumber.client.domain.Task
import org.plumber.client.domain.TaskExecutor
import org.plumber.client.domain.TaskResult
import org.plumber.client.domain.TaskType
import org.plumber.common.services.FileService
import org.plumber.common.services.ShellCommand

import org.plumber.client.domain.OsPackage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/18/14.
 */

@Slf4j
@Service
class YouTubeDownloadTaskType implements TaskType {

	@Autowired
	ShellCommand shell

	@Autowired
	FileService files

	Requirements requirements() {
		[
		        osTypes: [OSType.ALL],
				packages: [
				        new OsPackage(name:'youtube-dl')
				]
		]
	}

	@Override
	String name() {
		return "YouTubeDownload"
	}

	@Override
	TaskExecutor executor() {
		return new YouTubeDownloadTaskExecutor()
	}



	class YouTubeDownloadTaskExecutor implements TaskExecutor {

		String videoId, format

		ShellCommand.Result shellResult

		JobContext context

		Task task

		File download(String videoId, String format) throws Exception {
			File tempFile = files.tempFile()

			String youtubeCmd = "youtube-dl -o ${tempFile.absolutePath} https://www.youtube.com/watch?v=${videoId}"
			context?.log("You tube download command ${youtubeCmd}")

			shellResult = shell.execute(youtubeCmd)
			if (shellResult.out) {
				context?.log("OUT \n${shellResult.out}")
			}

			if (shellResult.err) {
				context?.log("ERR \n${shellResult.err}")
			}

			if (shellResult.success() && task)
				context?.values[task.id] = [
					result: tempFile.absolutePath
				]

			return tempFile
		}

		@Override
		TaskResult execute(JobContext context, Task task) throws Exception {
			this.context = context
			this.task = task

			//Set the arguments
			InvokerHelper.setProperties(this, task.args)


			download(videoId, format)

			TaskResult result = new TaskResult(
			        code: (shellResult.success()) ? TaskResult.Code.SUCCESS : TaskResult.Code.FAILURE,
					message: shellResult.err
			)

			context.log("Completed youtube download: ${result}", result)
			return result

		}
	}
}
