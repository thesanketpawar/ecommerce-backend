pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
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
                timeout(time: 5, unit: 'MINUTES') {
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
                stage('Deploy to ASG') {
            steps {
                sh """
                    aws autoscaling start-instance-refresh \
                        --auto-scaling-group-name ecommerce-app-asg \
                        --preferences '{"MinHealthyPercentage": 50, "InstanceWarmup": 90}' \
                        --region us-east-1
                """
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
