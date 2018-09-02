def label = "mypod-${UUID.randomUUID().toString()}"
podTemplate(label: label,
	containers: [
		containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),,\
		containerTemplate(name: 'selenium', image: 'registry.gitlab.com/icp-coc-tools/selenium-chrome:latest', pullPolicy: "Aways", ttyEnabled: true, command: 'cat')	
    ],
	volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]
	) {
	
	node(label) {
		
		stage('Get Source') {
		  	git "https://github.com/IBM-ICP-CoC/SumApp-Test.git"
		}
		  
		def props = readProperties  file:'pipeline.properties'
		def hostUrl = props['hostUrl']
		def runs = props['runs']
		def gitRepo = props['gitRepo']
		  
		stage('Build Maven project') {
			container('maven') {
				sh "mvn -B clean package"
			}
		}
		  
		stage('test') {
			container('selenium') {
		      	withCredentials([usernamePassword(credentialsId: 'github-id', usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
					sh "java -Dwebdriver.chrome.driver=/usr/bin/chromedriver -jar target/sumapp-test-1.0.0-jar-with-dependencies.jar -s ${hostUrl} -r ${runs} -p ${gitRepo} -u ${USERNAME} -t ${TOKEN}"				}
			}
		}
	}
  }   
   
   
   
 