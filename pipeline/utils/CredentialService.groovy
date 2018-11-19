/**
 * Permet d'éxecuter une closure avec une connexion gitlab
 *
 * @param credentialsId le credential
 * @param closure la closure
 */
def runWithCredentials(credentialsId, closure) {
    withCredentials([[$class: 'FileBinding', credentialsId: "${credentialsId}", variable: 'RSA_KEY']]) {
        final usr = sh(returnStdout: true, script: "whoami").trim()
        final configSSH
        if (usr == "root") {
            configSSH = "/root/.ssh/config"
        } else {
            configSSH = "/home/${usr}/.ssh/config"
        }
        writeFile file: "${configSSH}", text: "Host gitlab.lan.bdx.sqli.com\n\tIdentityFile ${env.RSA_KEY}"
        try {
            sh "git config user.name admin"
            sh "git config user.email jcodet@sqli.com"
            sh "git config push.default matching"
            closure()
        } finally {
            sh "rm ${configSSH}"
        }
    }
}

return this
