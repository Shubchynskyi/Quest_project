pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    // Сборка Docker образа приложения
                    sh 'docker-compose up --build -d'
                }
            }
        }

        stage('Integration Test') {
            agent {
                // Запуск контейнера с доступом к Docker сокету и привилегиями для выполнения тестов
                docker {
                    image 'maven:3.9.6-eclipse-temurin-21-jammy'
                    args '--privileged -v /var/run/docker.sock:/var/run/docker.sock'
                }
            }
            steps {
                script {
                    // Выполнение интеграционных тестов внутри контейнера с доступом к Docker
                    sh 'mvn verify'
                }
            }
        }
    }

    post {
        success {
            echo 'Build and Tests succeeded!'
        }
        failure {
            echo 'Build or Tests failed. Cleaning up...'
            script {
                sh 'docker-compose down'
                sh 'docker image prune -f'
            }
        }
        always {
            echo 'Pipeline finished.'
        }
    }
}
