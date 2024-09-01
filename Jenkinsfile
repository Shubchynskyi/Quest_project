pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Проверка исходного кода из репозитория
                    checkout scm
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Сборка Docker контейнера приложения
                    sh 'docker-compose up --build -d'
                }
            }
        }

        stage('Integration Test') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-21-jammy'
                    args '-v /var/run/docker.sock:/var/run/docker.sock --privileged'
                }
            }
            steps {
                script {
                    // Запуск интеграционных тестов
                    sh 'mvn verify'
                }
            }
        }
    }

    post {
        success {
            echo 'Сборка и тесты успешно выполнены!'
        }
        failure {
            echo 'Сборка или тесты не прошли, очистка...'
            script {
                sh 'docker-compose down'
                sh 'docker image prune -f'
            }
        }
        always {
            echo 'Пайплайн завершён.'
        }
    }
}
