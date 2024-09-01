pipeline {
    agent any

    environment {
        // Переменные окружения для Docker и Maven
        DOCKER_IMAGE = 'my-app-image'
        MAVEN_IMAGE = 'maven:3.9.6-eclipse-temurin-21-jammy'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Получаем исходный код из репозитория
                    checkout scm
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Строим Docker образ с приложением
                    sh 'docker build -t ${DOCKER_IMAGE} .'
                }
            }
        }

        stage('Run Application and Tests') {
            parallel {
                stage('Start Database') {
                    steps {
                        script {
                            // Запускаем контейнер базы данных
                            sh 'docker-compose up -d db'
                        }
                    }
                }

                stage('Run Integration Tests') {
                    agent {
                        docker {
                            image "${MAVEN_IMAGE}"
                            args '-v /var/run/docker.sock:/var/run/docker.sock --privileged'
                        }
                    }
                    steps {
                        script {
                            // Запускаем контейнер приложения и выполняем тесты
                            sh '''
                            docker-compose up -d app
                            docker-compose exec -T app mvn verify
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            // Действия при успешной сборке
            echo 'Build and tests were successful!'
        }

        failure {
            // Действия при неудачной сборке
            echo 'Build or tests failed, cleaning up...'
            script {
                sh 'docker-compose down'
                sh 'docker image prune -f' // Опционально, если вы хотите удалить неиспользуемые образы
            }
        }

        always {
            echo 'Cleanup complete.'
        }
    }
}
