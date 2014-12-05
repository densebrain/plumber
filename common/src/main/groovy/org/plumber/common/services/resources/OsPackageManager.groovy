package org.plumber.common.services.resources

import org.plumber.client.domain.OsPackage

/**
 * Created by jglanz on 11/17/14.
 */
interface OsPackageManager {

    List<OsPackage> list()

    OsPackage install(String name)

    void remove(OsPackage p)

    String name()

    Boolean available()

}