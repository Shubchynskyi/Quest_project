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
            // Cleanup Docker Resources
            sh './cleanup.sh'
            echo 'Build failed.'
        }
        success {
            script {
                echo 'Build succeeded! Restarting NGINX in 60 seconds...'
                // Delay before restarting NGINX
                sleep(time: 60, unit: 'SECONDS')
                // Restart NGINX
                sh 'docker exec webserver nginx -s reload'
            }
        }
    }
}