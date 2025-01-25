pipeline {

  agent any
  
  tools {
      maven 'maven-3.8.6' 
  }
  
  stages {
  
    stage("set up parameters"){
      steps{
         script { 
          properties([
           parameters([
                //string( defaultValue: '0.0.1', description: 'Version to build', name: 'VERSION')
                validatingString(defaultValue: '0.0.1', description: 'version to build', failedValidationMessage: 'This parameter is mandatory', name: 'VERSION', regex: '^(?!\\s*$).+'),
                validatingString(defaultValue: '', description: 'name of the docker repository', failedValidationMessage: 'This parameter is mandatory', name: 'DOCKER_REPO', regex: '^(?!\\s*$).+')
           ])
          ])
         }
       }
    }
    
    stage("jar build") {
    
      steps {
       sh "mvn clean install -DskipTests"
      }
    
    }
    
    stage("docker image build") {
    
      steps {
        
        sh "sudo docker build --tag ${params.DOCKER_REPO}/kulvida-be:${params.VERSION} ."
      }
    
    }
    
    stage("docker image tagging") {
    
      steps {
        sh "sudo docker rmi ${params.DOCKER_REPO}/kulvida-be:latest"
        sh "sudo docker tag ${params.DOCKER_REPO}/kulvida-be:${params.VERSION} ${params.DOCKER_REPO}/kulvida-be:latest"
      }
    
    }
    
    stage("docker image push to the registry") {
    
      steps {
        sh "sudo docker push ${params.DOCKER_REPO}/kulvida-be:${params.VERSION}"
        sh "sudo docker push ${params.DOCKER_REPO}/kulvida-be:latest"
      }
    
    }    
    
  
  }

}
