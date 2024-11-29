pipeline {
    agent any
    environment {
            DB_USERNAME = credentials('db-quests-app_USR')
            DB_PASSWORD = credentials('db-quests-app_PSW')
        }
    stages {

        stage('Debug Environment') {
            steps {
                script {
                    sh 'echo "DB_USERNAME=$DB_USERNAME"'
                    sh 'echo "DB_PASSWORD=$DB_PASSWORD"'
                }
            }
        }


        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Permissions') {
            steps {
                script {
                    sh 'chmod +x ./build-and-test.sh'
                    sh 'chmod +x ./deploy.sh'
                }
            }
        }

//         stage('Update .env') {
//             steps {
//                 script {
//                     sh '''
//                         sed -i "s|\\${DB_USERNAME}|$DB_USERNAME|g" .env
//                         sed -i "s|\\${DB_PASSWORD}|$DB_PASSWORD|g" .env
//                         '''
//                 }
//             }
//         }

        stage('Test and Build') {
            steps {
                script {
                    sh './build-and-test.sh'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh './deploy.sh docker-compose-server.yaml'
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

                // Remove all images that start with 'testcontainers'
                sh '''
                    docker images --format "{{.Repository}}:{{.Tag}}" | grep "^testcontainers" | while read image; do
                        docker rmi $image || true
                    done
                '''

                // Clean up unused Docker images
                sh 'docker image prune -f'
            }
        }
    }
}