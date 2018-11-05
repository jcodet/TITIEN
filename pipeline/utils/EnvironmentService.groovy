/**
 * Renvoie la Map avec les informations des schémas.
 *
 * On est obligé de passer par une méthode parce que le step "load" du pipeline plugin ne permet pas de charger
 * directement un attribut (ou en tout cas je ne sais pas comment le faire...).
 */

def env() {
    final env = [
            'INT'        : [
                    name                    : 'titien_integration',
                    ansible_env             : 'titien-souche-1.0.0',
                    label                   : 'TITIEN-SOUCHE-1.0.0',
                    tag                     : 'integration'
            ]
    ]
    return env
}

/**
 * Pré requis :
 * - création d'un user docker dans le group docker
 * - Génération d'une clé SSH sur l'env cible
 * - Ajout de cette clé dans le authorized_keys
 * - Désactiver le pull auto des clés
 * @param deploy_env
 */


def deployWithAnsible(ansible, deploy_env) {

    node(deploy_env.label) {
        def workspace = pwd()
        sh "rm -rf * .git"
        checkout scm

        ansible.pull()

        // Préparation du fichier private
        sh 'mkdir -p /applis/livraisons/'
        sh "cp ${workspace}/pipeline/resources/private.conf /applis/livraisons/private.conf"
        // Le fichier private est variabilisé pour ne stocker l'information qu'un seul endroit (variable d'environnement du node)
        sh "sed -i 's/%ORACLE_PASSWORD%/${deploy_env.db_password}/g' /applis/livraisons/private.conf"
        sh "sed -i 's/%AAA_PASSWORD%/${deploy_env.aaa_password}/g' /applis/livraisons/private.conf"
        sh "sed -i 's/%AAA_TRUST_STORE_PASSWORD%/${deploy_env.aaa_trust_store_password}/g' /applis/livraisons/private.conf"
        sh "sed -i 's/%DOCKER_REGISTRY%/${deploy_env.docker_registry}/g' /applis/livraisons/private.conf"

        // Préparation du fichier flyway
        sh "cp ${workspace}/pipeline/resources/flyway.properties /applis/livraisons/flyway.properties"
        // Le flyway properties est variabilisé pour ne stocker l'information qu'un seul endroit (variable d'environnement du node)
        sh "sed -i 's/%USER%/${deploy_env.db_user}/g' /applis/livraisons/flyway.properties"
        sh "sed -i 's/%PASSWORD%/${deploy_env.db_password}/g' /applis/livraisons/flyway.properties"
        sh "sed -i 's/%HOST%/${deploy_env.db_host}/g' /applis/livraisons/flyway.properties"
        sh "sed -i 's/%PORT%/${deploy_env.db_port}/g' /applis/livraisons/flyway.properties"
        sh "sed -i 's/%SERVICE%/${deploy_env.db_service}/g' /applis/livraisons/flyway.properties"

        // Utilisation du bon tag pour le container docker
        sh "sed -i 's/%VERSION%/${deploy_env.tag}/g' ${workspace}/deploy/livraison/config/docker-compose.yml"
        sh "sed -i 's/%DOCKER_REGISTRY_PACKAGE%/\\/rte\\/popcorn/g' ${workspace}/deploy/livraison/config/docker-compose.yml"

        // Copie des scripts de l'application vers le projet deploy (SSOT)
        sh "cp -R ${workspace}/application/database/sql/* ${workspace}/deploy/livraison/bdd/flyway/"

        // Préparation de la ligne de commande ansible
        // définition des variables version et env
        def ansible_command = "cd /livraison/installation;ansible-playbook -v site.yml " +
                "--inventory-file=/livraison/installation/inventory/${deploy_env.ansible_env} " +
                "--extra-vars \"version=${deploy_env.tag} env=${deploy_env.ansible_env}\""

        // On joue le playbook sur l'env cible
        sh "docker run --rm -e ANSIBLE_HOST_KEY_CHECKING=False -v /root/.ssh:/root/.ssh -v /applis/livraisons:/applis/livraisons " +
                "-v ${workspace}/deploy/livraison/:/livraison " +
                "${ansible.imageName()} " +
                "/bin/sh -c '${ansible_command}'"

    }
}

//un
def undeploy(deploy_env) {

    node(deploy_env.label) {
        // On undeploy l'application
        status = sh returnStatus: true, script: "systemctl disable POPCORN"
        status = sh returnStatus: true, script: "systemctl stop POPCORN"
    }
}

return this
