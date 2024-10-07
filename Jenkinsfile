pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Permissions') {
            steps {
                script {
                    sh 'chmod +x ./build-and-test.sh'
//                     sh 'chmod +x ./cleanup.sh'
                    sh 'chmod +x ./deploy.sh'
                }
            }
        }

        stage('Test and Build') {
            steps {
                script {
                    sh './build-and-test.sh'
                }
            }
        }

//         stage('Cleanup') {
//             steps {
//                 script {
//                     sh './cleanup.sh'
//                 }
//             }
//         }

        stage('Deploy') {
            steps {
                script {
                    sh './deploy.sh'
                }
            }
        }
    }

    post {
        failure {
            echo 'Build or deployment failed.'
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