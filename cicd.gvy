pipeline {
    agent any
    stages {
        stage('compile') {
	         steps {
                // step1 
                echo 'compiling..'
		            git url: 'https://github.com/lerndevops/PetClinic'
		            sh script: '/opt/maven/bin/mvn compile'
           }
        }
        stage('package/build-war') {
	         steps {
                // step5
                echo 'package......'
		            sh script: '/opt/maven/bin/mvn package'	
           }		
        }
        stage('build & push docker image') {
	         steps {
              withDockerRegistry(credentialsId: 'DOCKER_HUB_LOGIN', url: 'https://index.docker.io/v1/') {
                    sh script: 'cd  $WORKSPACE'
                    sh script: 'docker build --file Dockerfile --tag docker.io/lerndevops/petclinic:$BUILD_NUMBER .'
                    sh script: 'docker push docker.io/lerndevops/petclinic:$BUILD_NUMBER'
              }	
           }		
        }
    stage('Deploy-App-QA') {
  	   steps {
              sh 'ansible-playbook --inventory /tmp/inv $WORKSPACE/deploy/deploy-kube.yml --extra-vars "env=qa build=$BUILD_NUMBER"'
	   }
	   post { 
              always { 
                cleanWs() 
	      }
	   }
	}
    }
}
