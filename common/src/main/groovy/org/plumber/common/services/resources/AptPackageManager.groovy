package org.plumber.common.services.resources

import groovy.util.logging.Slf4j
import org.plumber.client.domain.OsPackage
import org.plumber.common.services.ShellCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/17/14.
 */

@Service
@Slf4j
class AptPackageManager extends BasePackageManager {

	@Autowired
	private ShellCommand shell;



	@Override
	List<OsPackage> list() {
		ShellCommand.Result result = shell.execute("sudo -n dpkg --get-selections | grep -v deinstall")
		if (!result.success())
			throw new RuntimeException(result.err);

		def leaves = result.out.split()
		def packages = []
		leaves.each { leave ->
			packages << new OsPackage(name: leave, description: leave, version: 'CURRENT')
		}
		return packages
	}

	@Override
	OsPackage install(String name) {
		ShellCommand.Result result = shell.execute("sudo -n apt-get install -y ${name}")
		if (!result.success())
			throw new RuntimeException(result.err)

		return new OsPackage(name: name, description: name, version: 'CURRENT')
	}

	@Override
	void remove(OsPackage p) {

	}

	@Override
	String name() {
		return "Aptitude"
	}

	@Override
	Boolean available() {
		ShellCommand.Result result = shell.execute("which apt-get")
		return result.success()
	}
}
