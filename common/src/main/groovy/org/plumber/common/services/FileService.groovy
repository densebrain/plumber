package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.plumber.common.Constants
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by jglanz on 11/18/14.
 */

@Service
@Slf4j
class FileService implements Constants {


	@Value('${file.storage.path:/tmp}')
	String fileStoragePath

	File dir, cacheDir, baseDirectory

	List<File> tempFiles = []

	@PostConstruct
	void setup() {
		dir = new File(fileStoragePath)
		dir.mkdirs()

		baseDirectory = new File('.')

		cacheDir = new File(CACHE)
		cacheDir.mkdirs()

		log.info("Using ${dir.absolutePath} for file storage");
	}

	@PreDestroy
	void cleanup() {
		List<File> toDelete = tempFiles.clone()

		toDelete.each { it->
			it.delete()
		}
	}

	File tempFile(String ext = "tmp") {
		File f = new File(dir, UUID.randomUUID().toString() + ".${ext}")
		tempFiles <<  f
		return f
	}

	void close(s) {
		try {
			s.close()
		} catch (e) {}
	}

}
