pipeline {
    agent {
        // Настраиваем Jenkins для использования Docker агента с доступом к Docker демону
        docker {
            image 'docker:latest'
            args '-v /var/run/docker.sock:/var/run/docker.sock --privileged'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                // Получаем исходный код из репозитория
                checkout scm
            }
        }

        stage('Build and Run') {
            steps {
                script {
                    // Запускаем Docker контейнеры с помощью основного docker-compose.yml
                    sh "docker-compose up --build -d"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Запускаем тесты; доступ к Docker внутри контейнера будет через сокет
                    sh 'docker-compose exec <название контейнера> ./run-tests.sh' // Замените на ваш контейнер и команду тестов
                }
            }
        }
    }

    post {
        success {
            // Действия при успешной сборке
            echo 'Build was successful!'
        }

        failure {
            // Действия при неудачной сборке
            echo 'Build failed, cleaning up...'
            script {
                sh 'docker-compose down'
                sh 'docker image prune -f' // Опционально, если вы хотите удалить неиспользуемые образы
            }
        }

        always {
            echo 'Always block done...'
        }
    }
}