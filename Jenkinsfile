pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Run') {
            steps {
                script {
                    sh "docker-compose up --build -d"
                }
            }
        }

        stage('Integration Test') {
            steps {
                script {
                    // Замените 'quests-app' на имя контейнера
                    sh "docker-compose exec --privileged quests-app mvn clean verify"
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