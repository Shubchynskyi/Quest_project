pipeline {
    agent any

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

                    // Пересборка без использования кэша
//                     sh "docker-compose build --no-cache"
//                     sh "docker-compose up -d"
                }
            }
        }

        stage('Test') {
            steps {
                // Здесь вы можете добавить шаги для тестирования вашего приложения
                echo 'Running tests...'
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
//                 sh 'docker image prune -f' // Опционально, если вы хотите удалить неиспользуемые образы
            }
        }

        always {
            echo 'Always block done...'
        }
    }
}