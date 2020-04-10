# Project 1 - cs122b-spring20-team-80 
cs122b-spring20-team-80 created by GitHub Classroom
submitted on April 10th, 2020

- Implementation of the Movie List, Single Movie and Single Star page of the Fablix Application.

## Demo video URL

[link](https://youtu.be/Cd_2F8tFhRM)


## Instruction of deployment

Followed the exact same process of deployment as described on Canvas. 

  1. Ssh into AWS and run MySQL and Tomcat.
  2. Git clone git repository to AWS instance.
  
```bash
git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80.git
```
  3. Build war file inside repository.
  
```bash
mvn package
```
  4. Copy newly built war file to Tomcat webapps folder.
```bash
cp ./target/*.war /home/ubuntu/tomcat/webapps
```
  5. Launch the app from the Tomcat manager page on localhost.

## Contribution

#### Kinjal Reetoo 
  - Implemented Movie List and Single Star pages.

#### Yasvi Patel 
  - Implemented Single Movie page, hyperlinks and jump requirements.
  - Demo video and final deployment for grading.
  

