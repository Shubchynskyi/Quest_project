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
                    // Сборка образа без выполнения тестов
                    sh "docker-compose up --build -d"
                }
            }
        }

        stage('Integration Test') {
            steps {
                script {
                    // Запуск интеграционных тестов внутри работающего контейнера с доступом к Docker
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
                sh 'docker-compose down'
                sh 'docker image prune -f'
            }
        }

        always {
            echo 'Always block done...'
        }
    }
}
