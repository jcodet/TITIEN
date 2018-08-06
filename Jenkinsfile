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
      stage('build') {
         steps {
           echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
           sh 'mvn clean install'
           junit 'target/surefire-reports/**/*.xml'
         }
         post {
            always {
               junit 'build/reports/**/*.xml'
               echo 'This will always run'
            }
            success {
               echo 'This will run only if successful'

            }
            failure {
               echo 'This will run only if failed'
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
   }

}
