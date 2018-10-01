/**
 * Récupère la version du pom sans le SNAPSHOT
 * @param pomPath la path vers le pom
 * @return la version
 */
def getReleaseVersion(pomPath = './') {
    def matcher = readFile(pomPath + 'pom.xml') =~ '<version>(.+)-SNAPSHOT</version>'
    releaseVersion = matcher[0][1]
    return releaseVersion
}

/**
 * Renvoie la Map avec les noms des images à utiliser pour l'outil choisi.
 *
 * On est obligé de passer par une méthode parce que le step "load" du pipeline plugin ne permet pas de charger
 * directement un attribut (ou en tout cas je ne sais pas comment le faire...).
 */
def images() {
    final images = [
            maven  : 'registry-private.docker.iscbordeaux.lan.bdx.sqli.com/rte-build/maven:3.3.9-jdk-8',
            sqlplus: 'registry-private.docker.iscbordeaux.lan.bdx.sqli.com/sqlplus:12.1',
            scanner: "${env.SONAR_SCANNER_IMAGE}",
            flyway : 'registry-private.docker.iscbordeaux.lan.bdx.sqli.com/flyway:4.0.3',
            node   : 'node:6.11.5',
            ansible: 'registry-private.docker.iscbordeaux.lan.bdx.sqli.com/rte/ansible_flyway_4.2.0:centos7'
    ]
    return images
}

return this
