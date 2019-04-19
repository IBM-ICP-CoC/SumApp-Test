/*

This pipeline script requires the following parameters to be set:

src     : the URL of the source (assumes it is a public project)
hostUrl : the URL of the SumApp host
runs    : the number of runs to try
gitRepo : the gitHub repository to report teh results to

It also expects there to be a Jenkins credential github-id used to access GitHub with (to write a new issue).

*/
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
			git "${src}"
		}
		  
		stage('Build Maven project') {
			container('maven') {
				sh "mvn -B clean package"
			}
		}
		  
		stage('test') {
			container('selenium') {
		      	withCredentials([usernamePassword(credentialsId: 'github-id', usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
					sh "java -Dwebdriver.chrome.driver=/usr/bin/chromedriver -jar target/sumapp-test-1.0.0-jar-with-dependencies.jar -s ${hostUrl} -r ${runs} -p ${gitHubRepo} -u ${USERNAME} -t ${TOKEN}"				}
			}
		}
	}
  }   
   
   
   
 
