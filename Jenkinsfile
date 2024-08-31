pipeline {
    agent {
        docker {
            image 'amazoncorretto:21-alpine-full' // Используем образ с Java 21 от Amazon Corretto
            args '--privileged -v /var/run/docker.sock:/var/run/docker.sock' // Даем доступ к Docker сокету
        }
    }

    stages {
        stage('Checkout') {
            steps {
                // Получаем исходный код из репозитория
                checkout scm
            }
        }

        stage('Build and Run Containers') {
            steps {
                script {
                    // Сборка образа и запуск контейнеров с помощью docker-compose
                    sh "docker-compose up --build -d"
                }
            }
        }

        stage('Integration Test') {
            steps {
                script {
                    // Запуск тестов внутри работающего контейнера с доступом к Docker
                    // Убедитесь, что 'quests-app' соответствует имени вашего контейнера
                    sh "docker-compose exec --privileged quests-app mvn verify"
                }
            }
        }
    }

    post {
        success {
            echo 'Build was successful!'
        }

        failure {
            echo 'Build failed, cleaning up...'
            script {
                // Останавливаем контейнеры и очищаем ненужные образы
                sh 'docker-compose down'
                sh 'docker image prune -f'
            }
        }

        always {
            echo 'Always block done...'
        }
    }
}
