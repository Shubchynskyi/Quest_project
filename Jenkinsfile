pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Fetch the source code from the repository
                checkout scm
            }
        }

        stage('Cleanup Docker Resources') {
            steps {
                script {
                    // Run cleanup script before building
                    sh './cleanup.sh'
                }
            }
        }

        stage('Build and Run Application') {
            steps {
                script {
                    // Run the main script to build and run the application
                    sh './build-and-run.sh'
                }
            }
        }
    }

    post {
        failure {
            echo 'Build failed.'
        }
        success {
            echo 'Build succeeded!'
        }
    }
}