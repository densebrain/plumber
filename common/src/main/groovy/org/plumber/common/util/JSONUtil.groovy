package org.plumber.common.util

import groovy.json.JsonSlurper
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/19/14.
 */


class JSONUtil {

	static Object json(URL url) {
		def slurper = new JsonSlurper()
		slurper.parse(url)
	}
}
