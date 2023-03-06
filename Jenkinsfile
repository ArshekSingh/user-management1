pipeline {
    agent any
    environment {
    AWS_ACCOUNT_ID='973152351290'
    AWS_DEFAULT_REGION='ap-south-1'
    ENVIRONMENT='non-prod'
    CUSTOMER_NAME='svcl'
    PRODUCT='finncub'
    APP='usermanagement'
    }
    stages {

        stage('Clone Repo') {
            steps {
                cleanWs()
				checkout([$class: 'GitSCM', branches: [[name: '*/uat']],
				doGenerateSubmoduleConfigurations: true, extensions: [],
				submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'BitbucketCreds',
				url: 'https://SasDevOpsAdmin@bitbucket.org/finstudio/user-management.git']]])
                
            }
        }
        stage('Logging into AWS ECR') {
            steps {
                script {
                    sh 'aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com'
                }
            }

        }
        stage('Building image') {
            steps{
                script {
                sh 'git submodule update --init --recursive'
                withDockerContainer(image:'maven:latest',args:'--entrypoint=""') {
                     sh 'mvn clean install'
                }
				sh 'docker build -t ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service .'
                }
            }
        }
		stage('Pushing to ECR') {
            steps{
                script {
                    sh 'docker tag ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER}'
                    sh 'docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER}'
                    sh 'docker rmi ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER} --force'
                }
            }
        }
        stage('Triggering CD Job') {
            steps{
                script {
                    build job: "non-prod-svcl-finncub-services-deploy-pipeline",
                    parameters: [
                        [ $class: 'StringParameterValue', name: 'ImageNumber', value: "${BUILD_NUMBER}"],
                        [ $class: 'StringParameterValue', name: 'APP', value: "${APP}"]                      
                    ]
                }
            }
        }
    }
}
