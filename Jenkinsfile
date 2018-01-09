pipeline {
  agent any
  stages {
    stage('Config System') {
      steps {
        echo 'Setup the system'
        sh '/etc/profile.d/exports.sh'
        echo 'wget, curl, java, sbt and spark are now installed by Config Management system :)'
        sh 'sudo echo $DEVOPS_CONF_DIR'
        sh 'sudo cp -Rf conf/* $DEVOPS_CONF_DIR'
      }
    }
    stage('Test the System') {
      steps {
        sh 'sudo ls $DEVOPS_CONF_DIR'
        sh 'java -version'
        sh 'sbt about'
      }
    }
    stage('Test scalatest') {
      steps {
        sh 'sudo sbt clean test'
        archiveArtifacts 'target/test-reports/*.xml'
      }
    }
    stage('Build') {
      steps {
        sh 'sbt clean compile package'
        archiveArtifacts 'target/*/*.jar'
      }
    }
    stage('Deploy') {
      steps {
        sh 'sudo cp target/*/*.jar /opt/deploy/batchETL'
      }
    }
  }
}