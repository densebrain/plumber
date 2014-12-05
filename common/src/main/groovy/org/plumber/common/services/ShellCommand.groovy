package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.plumber.common.util.SystemCommandExecutor
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/17/14.
 */

@Service
@Slf4j
class ShellCommand {

    Result execute(String...parts) {
        List<String> commands = ["/bin/bash", "-c"]
        commands += Arrays.asList(parts);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands)
        int result = commandExecutor.executeCommand()

        String out = commandExecutor.getStandardOutputFromCommand().toString()
        String err = commandExecutor.getStandardErrorFromCommand().toString()

        if (result == 0)
            log.trace('Executed commands {} \n Output {} \n Error {}\n Result Code {}', commands, out, err, result)
        else
            log.warn('Executed commands {} \n Output {} \n Error {}\n Result Code {}', commands, out, err, result)

        return new Result(code: result, out: out, err: err)
    }

    static class Result {

        String out, err

        Integer code

        Boolean success() {
            return code == 0
        }

    }
}


