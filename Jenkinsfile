pipeline {
    agent any
    environment {
    AWS_ACCOUNT_ID='305949049023'
    AWS_DEFAULT_REGION='ap-south-1'
    app='user'
    IMAGE_REPO_NAME='sastech-devops-repository'
    IMAGE_TAG='account-${BUILD_NUMBER}t'
    REPOSITORY_URI = '${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}'
    AWS_ACCESS_KEY_ID     = credentials('access_key')
    AWS_SECRET_ACCESS_KEY = credentials('secret_key')
    }
    
    stages {

        stage('Clone Repo') {
            steps {
				checkout([$class: 'GitSCM', branches: [[name: '*/devops']],
				doGenerateSubmoduleConfigurations: true, extensions: [],
				submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'bitbucket_creds',
				url: 'https://vinodkr1@bitbucket.org/finstudio/user-management.git']]])
                
            }
        }
        stage('Logging into AWS ECR') {
            steps {
                script {
                    sh 'aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 305949049023.dkr.ecr.ap-south-1.amazonaws.com'
                }
            }

        }
        stage('Building image') {
            steps{
                script {
                sh 'git submodule update --init --recursive'
                sh 'mvn clean install'
                sh 'cp /var/lib/jenkins/user-1.0.0.jar .'
				sh 'docker build -t sastech-devops-repository .'
                }
            }
        }
		stage('Pushing to ECR') {
            steps{
                script {
                    sh 'docker tag sastech-devops-repository:latest 305949049023.dkr.ecr.ap-south-1.amazonaws.com/sastech-devops-repository:${app}-${BUILD_NUMBER}'
                    sh 'docker push 305949049023.dkr.ecr.ap-south-1.amazonaws.com/sastech-devops-repository:${app}-${BUILD_NUMBER}'
                }
            }
        }
    }
    post {
        always {
            script {
                if (currentBuild.currentResult == 'SUCCESS') {
                    emailext subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - SUCCESS!!!',
                    body: '$DEFAULT_CONTENT',
                    recipientProviders: [
                    [$class: 'RequesterRecipientProvider']
                    ],
                    replyTo: '$DEFAULT_REPLYTO',
                    to: 'ranjith5076457@gmail.com'
                }
                if (currentBuild.currentResult == 'FAILURE') {
                    emailext subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - FAILED!!!',
                    body: '$DEFAULT_CONTENT',
                    recipientProviders: [
                    [$class: 'RequesterRecipientProvider']
                    ],
                    replyTo: '$DEFAULT_REPLYTO',
                    to: 'ranjith5076457@gmail.com'
                }
            }
        }
    }
}