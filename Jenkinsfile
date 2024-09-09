pipeline {
    agent any

    stages {
        stage('Cleanup Docker Resources') {
            steps {
                script {
                    // Запуск скрипта очистки перед сборкой
                    sh './cleanup.sh'
                }
            }
        }

        stage('Build and Run Application') {
            steps {
                script {
                    // Запуск основного скрипта для сборки и запуска приложения
                    sh './build-and-run.sh'
                }
            }
        }
    }

    post {
        always {
            echo 'Сборка завершена. Выполняется финальная очистка...'
        }
        failure {
            echo 'Сборка завершилась с ошибкой.'
        }
        success {
            echo 'Сборка завершилась успешно!'
        }
    }
}
