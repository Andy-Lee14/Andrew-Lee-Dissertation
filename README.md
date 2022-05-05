<!-- IMPORTANT: This README should be viewed in Preview Mode -->
<h1 align="center">SAFER GAMBLING APP - <i>PROOF OF CONCEPT</i></h1>

<h3 align="center">SHEFFIELD HALLAM - CONSULTANCY PROJECT </h3>
<hr>

<h3 align="center">Languages and Tools Used:</h3>
<p align="center">
<a href="https://www.jetbrains.com/idea/download/#section=mac" target="_blank" rel="noreferrer"> <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPRXbtyxShvghvmUTibPVXbz0ytDIW2ZNW1w&usqp=CAU" alt="intelliJ" width="40" height="40"/> </a>
<a href="https://www.scala-lang.org" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/scala/scala-original.svg" alt="scala" width="40" height="40"/> </a>
<a href="https://kubernetes.io" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/kubernetes/kubernetes-icon.svg" alt="kubernetes" width="40" height="40"/> </a>
<a href="https://www.docker.com/" target="_blank" rel="noreferrer"><img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original-wordmark.svg" alt="docker" width="40" height="40"/> </a> 
<a href="https://www.jenkins.io" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/jenkins/jenkins-icon.svg" alt="jenkins" width="40" height="40"/> </a>
<a href="https://jfrog.com/artifactory/" target="_blank" rel="noreferrer"> <img src="https://miro.medium.com/max/400/1*klTJGBP5RDXmK2U2_xp8Ow.png" alt="jenkins" width="40" height="40"/> </a>
<a href="https://slack.com/" target="_blank" rel="noreferrer"> <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Slack_icon_2019.svg/1024px-Slack_icon_2019.svg.png" alt="kibana" width="40" height="40"/> </a>
<a href="https://prometheus.io/" target="_blank" rel="noreferrer"> <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/Prometheus_software_logo.svg/2066px-Prometheus_software_logo.svg.png" alt="kibana" width="40" height="40"/> </a>
<a href="https://grafana.com" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/grafana/grafana-icon.svg" alt="grafana" width="40" height="40"/> </a>
<a href="https://www.elastic.co" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/elastic/elastic-icon.svg" alt="elasticsearch" width="40" height="40"/> </a> 
<a href="https://www.elastic.co/kibana" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/elasticco_kibana/elasticco_kibana-icon.svg" alt="kibana" width="40" height="40"/> </a> 
</p>

<hr>

Welcome to the **Safer Gambling App!** This application has been designed to identify certain customer behaviours that could indicate that they are at risk of developing a gambling addiction. Once identified at risk customer activity will be made visible to Kibanna and Grafana dashboards via metrics exposed to Prometheus. Once the relevant dashboards have ingested this data, they will be ready to be reviewed by SBG’s customer outreach team. But how will they know when to check these dashboards, I hear you say? Not to worry, The Safer Gambling App has got it covered, with its built in Slack notification system! Follow the instructions below to fire it up.

<hr>

<h4>QUICK START GUIDE</h4>

***NOTE:*** *'For best results it is highly recommended that this project be viewed in the IntelliJ development environment. Clicking on the IntelliJ logo in the languages and tools section will open a page where the IDE can be downloaded'* 

**STEP 1:** Logging in and deploying the application.
```
pscli kubectl login
```
**STEP 2:** Use SBG login credentials. 
```
<01smithj>
<secure_SIG_password>
```
**STEP 3:** Select the applicable cluster from the list, in this case `test-app-guea`.

<img src="/Users/Andrew.Lee3/Documents/Sheffield Hallam/safer-gambling-app-stash/andrew_lee_shu_final_project/images/cluster_options.png" alt="cluster_options" width="500" height="220"/>

**STEP 4:** Select the appropriate namespace from the list.
```
core-ngp-cc-test
```
**STEP 5:** Now you are in the appropriate namespace `cd` into the filepath in which you have the **Safer Gambling App** repo stored.
```
cd Documents/Projects/Repos/safer-gambling-app
```
**STEP 6:** Deploy the **Safer Gambling App** with the following command.
```
kubectl apply -k safer-gambling-app-chronjob/overlays/test/test-guea
```
**STEP 7:** Check that Kubernetes has spun up an instance of the application and it has entered into a running state.
```
 kubectl -n core-ngp-cc-test get pods -w
```
**STEP 8:** View the application logs to check data is being processed correctly 
```
kubectl -n core-ngp-cc-local logs -f <insert pod name here>
```
**STEP 9:** Once the app has finished processing the data, it will trigger the at risk gambler list to be posted to the Slack channel `safer-gambling-app`, along with links to the Grafana and Kibana dashboards ready to be reviewed by the customer outreach team. (See an example of the Slack message below)

<img src="/Users/Andrew.Lee3/Documents/Sheffield Hallam/safer-gambling-app-stash/andrew_lee_shu_final_project/images/slack_message.png" alt="cluster_options" width="450" height="650"/>

<hr>

**KUBERNETES CONFIG**

The K8s config for this project has been added to the repo in the directory `safer-gambling-app-chronjob`. The usual convention in the CPG squad is to store all of its K8S configs in a separate repository called the K8S-Catalogue. The reason for this is that it allows the squad to make tweaks and changes to the config without the need to redeploy any of their applications. This minimises disruption to the apps. 

<hr>

**TIPS TO TRY:**
- Configure the frequency at which the app runs
- Configure how many standard deviations form the Mean you wish to see in the data
- Configure resources allocated to the application
- Increase/decrease the size and number of batches 
- Use `resources/customerData2.csv` to apply a different data set for the app to process

*"Currently some of these options are hard coded and require redeployment of the application to run them in CPG's test cluster however moving forward the application would add these variables to the `ApplicationConfig` file so that alterations can be made without the need to rebuild the app."*
  
<hr>

**CLEAN UP**

As this is just a prototype it is important to delete the instance of the application after use to ensure that resources are not being wasted. To do this enter the following command:
```
kubectl delete -k safer-gambling-app-chronjob/overlays/test/test-guea
```