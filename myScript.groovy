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

return this
