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
            maven  : 'maven:3.5.4-jdk-10-slim'
    ]
    return images
}

return this
