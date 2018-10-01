/**
 * Delete les volumes qui ne sont plus attachés à aucun container.
 */
def deleteDanglingVolumes() {
    deleteIfCountNotEmpty("docker volume rm", "docker volume ls -q -f dangling=true")
}

/**
 * Delete les images qui ne sont plus utilisées
 * @return
 */
def deleteDanglingImages() {
    deleteIfCountNotEmpty("docker rmi", "docker images -q -f dangling=true")
}

/**
 * Exécute la commande delete si le nombre de lignes en retour de la commande count est supérieur à 0.
 *
 * L'intérêt de cette méthode est de pouvoir lancer la commande même quand la liste est vide, ce qui occassione un
 * code de retour dans le step "sh" du Pipeline plugin si on le fait sur une seul ligne.
 *
 * @param deleteShellCmd la commande shell qui efface les éléments.
 * @param countShellCmd la commande shell qui liste les éléments à effacer.
 */
def deleteIfCountNotEmpty(final deleteShellCmd, final countShellCmd) {
    if (countLines(countShellCmd) > 0) {
        sh "${deleteShellCmd} \$(${countShellCmd})"
    }
}

/**
 * @param shellCommand la commande dont on calculera le nombre de lignes
 * @return le nombre de lignes du résultat de la commande, ou 0 si c'est vide.
 */
def countLines(final shellCommand) {
    final danglingFile = '/tmp/docker_dangling_nb.txt'
    sh "${shellCommand} | wc -l > ${danglingFile}"

    return readFile(danglingFile).toInteger()
}

return this
