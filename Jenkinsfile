pipeline {
   agent any
   tools {
     maven 'M3'
     jdk 'jdk8'
   }
   stages {
      stage ('Initialize') {
         steps {
            echo "PATH = ${PATH}"
            echo "M2_HOME = ${M2_HOME}"
         }
      }
      stage('Checkout') {
         steps {
            checkout scm
         }
      }
      stage('build') {
         steps {
           echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
           sh 'mvn clean install -Dmaven.test.failure.ignore=true'
         }
         post {
            always {
              echo 'Enregistrement des résultats de tests dans:'
            }
            success {
               echo 'This will run only if successful'
               mail subject:'TITIEN build result: OK',
                    body: 'Result of the build is OK',
                    to: 'julien.codet@gmail.com'
            }
            failure {
               echo 'This will run only if failed!!'
            }
            unstable {
               echo 'This will run only if the run was marked as unstable'
            }
            changed {
               echo 'This will run only if the state of the Pipeline has changed'
               echo 'For example, if the Pipeline was previously failing but is now successful'
            }
         }
      }
      stage('Publish test results') {
         steps {
            junit '**/surefire-reports/*.xml'
         }

      }
   }

}
