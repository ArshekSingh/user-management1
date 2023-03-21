def sendEmail(String subject, String attach ) {
  emailext body: '''Hello Team,
  Attached is the report for user-management-service.
  Please review the report and take necessary actions to fix any vulnerabilities found.

  Regards,
  DevOps Team.''',
  subject: subject,
  to: 'finncub.dev@sastechstudio.com',
  attachmentsPattern: attach
}
pipeline {
    agent any
    environment {
    AWS_ACCOUNT_ID='973152351290'
    AWS_DEFAULT_REGION='ap-south-1'
    ENVIRONMENT="${params.ENVIRONMENT}"
    CUSTOMER_NAME='svcl'
    PRODUCT='finncub'
    APP='usermanagement'
    }
    stages {

        stage('Clone Repo') {
            steps {
                cleanWs()
				checkout([$class: 'GitSCM', branches: [[name: '*/'+params.BRANCH]],
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
                withDockerContainer(image:'maven:latest',args:'--entrypoint="" -v $HOME/.m2:/root/.m2') {
                     sh 'mvn clean install'
                }
                sh 'hadolint Dockerfile > newfile.txt'
                sh 'cat newfile.txt'
				sh 'docker build -t ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service .'
                }
            }
        }
		stage('Pushing to ECR') {
            steps{
                script {
                    sh 'docker tag ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER}'
                    sh 'trivy image --format json --scanners vuln --severity HIGH,CRITICAL ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:latest  | jq -r .Results[].Vulnerabilities[] > high_critical_scan_results.json'
                    sh 'trivy image --format json --scanners vuln --severity LOW,MEDIUM ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:latest | jq -r .Results[].Vulnerabilities[] > low_medium_scan_results.json'
                    sendEmail("[High,Critical]-${ENVIRONMENT}-Trivy vulnerabilities Scan Report for [user-management] service","high_critical_scan_results.json")
                    sendEmail("[Low,Medium]-${ENVIRONMENT}-Trivy vulnerabilities Scan Report for [user-management] service","low_medium_scan_results.json")
                    sh 'docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER}'
                    sh 'docker rmi ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:${BUILD_NUMBER} -f'
                    sh 'docker rmi ${ENVIRONMENT}-${CUSTOMER_NAME}-${PRODUCT}-${APP}-service:latest'
                }
            }
        }
        stage('Triggering CD Job') {
            steps{
                script {
                    build job: "${ENVIRONMENT}-svcl-finncub-services-deploy-pipeline",
                    parameters: [
                        [ $class: 'StringParameterValue', name: 'ImageNumber', value: "${BUILD_NUMBER}"],
                        [ $class: 'StringParameterValue', name: 'APP', value: "${APP}"]
                    ]
                }
            }
        }
    }
    post {
     always {
         cleanWs()
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
