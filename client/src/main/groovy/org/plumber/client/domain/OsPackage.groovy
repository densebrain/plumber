package org.plumber.client.domain

import groovy.transform.ToString

/**
 * Created by jglanz on 11/17/14.
 */

@ToString
class OsPackage {

    String name, description, version

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof OsPackage)) return false

        OsPackage osPackage = (OsPackage) o

        if (name != osPackage.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}
