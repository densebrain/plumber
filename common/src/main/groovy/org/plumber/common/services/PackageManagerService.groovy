package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.OsPackage
import org.plumber.common.services.resources.OsPackageManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 11/17/14.
 */

@Service
@Slf4j
class PackageManagerService {


    @Autowired
    private BeanService beanService

    @Autowired
    private ReflectionService reflectionService

    private OsPackageManager manager

    private List<OsPackage> installedPackages = []

    @PostConstruct
    void setup() {
        List<OsPackageManager> managers = availablePackageManagers()
        log.info('Resource Managers Available: {}', managers)

        if (!managers) {
            log.error("No available package managers")
            return
        }

        manager = managers[0]
        log.info('Using {}', manager.name())

        updateInstalledPackages()
    }

    void updateInstalledPackages() {
        installedPackages = []

        manager.list().each { p ->
            log.info('Installed package {}-version-{}', p.name, p.version)
            installedPackages += p
        }
    }

    void ensurePackage(OsPackage p) {
        log.debug("Ensuring Package: {}", p)
        if (installedPackages.contains(p))
            return

        log.info("Installing package: {}", p)
        manager.install(p.name)
        updateInstalledPackages()
    }

    List<OsPackageManager> availablePackageManagers() {

        Set<Class<? extends OsPackageManager>> clazzes = reflectionService.getSubTypesOf(OsPackageManager.class)

        def managers = [];

        clazzes.each { clazz ->
            OsPackageManager manager = beanService.get(clazz);

            log.debug("Manager ${manager}")
            if (manager != null && !managers.contains(manager) && manager.available()) {
                log.info("Found package manager: {}", manager.name())
                managers << manager
            }

        }


        return managers
    }
}
