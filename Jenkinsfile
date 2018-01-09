pipeline {
  agent any
  stages {
    stage('Config System') {
      steps {
        sh 'cat /opt/env'
        sh 'echo "$DEVOPS_CONF_DIR"'
        echo 'Setup the system'
        echo 'wget, curl, java, sbt and spark are now installed by Config Management system :)'
        sh 'sudo cp -Rf conf/BatchETL.conf $(cat /etc/env)'
        sh 'sudo cp -Rf conf/BatchETL_staging.conf $DEVOPS_CONF_DIR'
      }
    }
    stage('Test the System') {
      steps {
        sh 'java -version'
        sh 'sbt about'
      }
    }
    stage('Test scalatest') {
      steps {
        sh 'sbt clean test'
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