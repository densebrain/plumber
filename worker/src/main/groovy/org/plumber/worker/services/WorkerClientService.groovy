package org.plumber.worker.services

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 12/3/14.
 */
@Service
@ConditionalOnProperty(name='manager', havingValue = 'false')
class WorkerClientService extends WorkerService {

}
