pipeline {
    agent any
    environment {
    AWS_ACCOUNT_ID='305949049023'
    AWS_DEFAULT_REGION='ap-south-1'
    app='usermanagement'
    IMAGE_REPO_NAME='sastech-devops-repository'
    IMAGE_TAG='account-${BUILD_NUMBER}'
    REPOSITORY_URI = '${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}'
    AWS_ACCESS_KEY_ID     = credentials('access_key')
    AWS_SECRET_ACCESS_KEY = credentials('secret_key')
    }
    stages {

        stage('Clone Repo') {
            steps {
				checkout([$class: 'GitSCM', branches: [[name: '*/develop']],
				doGenerateSubmoduleConfigurations: true, extensions: [],
				submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'BItBucket_devops_admin',
				url: 'https://devops_admin_sas@bitbucket.org/finstudio/user-management.git']]])
                
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
                sh 'cp /var/lib/jenkins/disbursement-1.0.0.jar .'
				sh 'docker build -t sastech-devops-repository-${app} .'
                }
            }
        }
		stage('Pushing to ECR') {
            steps{
                script {
                    sh 'docker tag sastech-devops-repository-${app}:latest 305949049023.dkr.ecr.ap-south-1.amazonaws.com/sastech-devops-repository:${app}-${BUILD_NUMBER}'
                    sh 'docker push 305949049023.dkr.ecr.ap-south-1.amazonaws.com/sastech-devops-repository:${app}-${BUILD_NUMBER}'
                }
            }
        }
        stage('Triggering CD Job') {
            steps{
                script {
                    build job: "../CD-Micro-Services/User-Management",
                    parameters: [
                        [ $class: 'StringParameterValue', name: 'ImageNumber', value: "${BUILD_NUMBER}"],
                        [ $class: 'StringParameterValue', name: 'Branch', value: "develop"],
                        [ $class: 'StringParameterValue', name: 'env', value: "uat"],
                        [ $class: 'StringParameterValue', name: 'cluster_name', value: "SASTech-Devops-Preprod"]                      
                    ]
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
                    to: 'finncub.dev@sastechstudio.com'
                }
                if (currentBuild.currentResult == 'FAILURE') {
                    emailext subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - FAILED!!!',
                    body: '$DEFAULT_CONTENT',
                    recipientProviders: [
                    [$class: 'RequesterRecipientProvider']
                    ],
                    replyTo: '$DEFAULT_REPLYTO',
                    to: 'finncub.dev@sastechstudio.com'
                }
            }
        }
    }
}
