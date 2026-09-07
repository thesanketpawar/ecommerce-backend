pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        DB_PASSWORD = credentials('db-password')
        JWT_SECRET = credentials('jwt-secret')
        IMAGE_NAME = "thesanketpawar/ecommerce-backend"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DEPLOY_HOST = "10.0.10.130"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/thesanketpawar/ecommerce-backend.git',
                    credentialsId: 'github-creds'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Docker Push') {
            steps {
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker push ${IMAGE_NAME}:latest"
            }
        }

        stage('Deploy to App Server') {
            steps {
                sshagent(credentials: ['app-server-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no -J ubuntu@34.232.51.82 ubuntu@${DEPLOY_HOST} '
                            docker pull ${IMAGE_NAME}:latest &&
                            docker stop ecommerce-app || true &&
                            docker rm ecommerce-app || true &&
                            docker run -d --name ecommerce-app -p 8080:8080 \
                                -e DB_HOST=ecommerce-db.cmrm6oggi5qh.us-east-1.rds.amazonaws.com \
                                -e DB_NAME=ecommerdb \
                                -e DB_USERNAME=postgres \
                                -e DB_PASSWORD=${DB_PASSWORD} \
                                -e JWT_SECRET=${JWT_SECRET} \
                                ${IMAGE_NAME}:latest
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs above.'
        }
    }
}
