# Project 2 - cs122b-spring20-team-80 
cs122b-spring20-team-80 created by GitHub Classroom
submitted by Kinjal Reetoo and Yasvi Patel on April 10th, 2020.

- Developing Fabflix Website.

## Demo video URL

[link](https://youtu.be/Cd_2F8tFhRM)

## Substring matching design

We used the mysql command  LIKE "%ABC%"  for the movie title, director name and star name search parameters; this command is case insensitive and finds any string that contains the pattern ABC anywhere in the string. For the year parameter we used exact match.


## Instruction of deployment

Followed the exact same process of deployment as described on Canvas. 

  1. Ssh into AWS and run MySQL and Tomcat.
  2. Git clone git repository to AWS instance.
  
```bash
git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80.git
```
  3. Build war file inside project repository.
  
```bash
cd /home/ubuntu/cs122b-spring20-team-80
mvn clean package
```
  4. Copy newly built war file to Tomcat webapps folder.
```bash
cp ./target/*.war /home/ubuntu/tomcat/webapps
```
  5. Launch the app from the Tomcat manager page on localhost.

## Substring Matching Protocol

## Contribution

### Kinjal Reetoo 
  - Implemented Movie List and Single Star pages.
  - Initial git setup and wrote README.md for P1.
  - Implemented the Main Page with Search and Browse
  - Implemented the Shopping Cart
  - Demo video and final deployment for grading P2.

### Yasvi Patel 
  - Implemented Single Movie page, hyperlinks and jump requirements.
  - Demo video and final deployment for grading P1.
  - Implemented the Login Page
  - Implement the Movie List Page, Single Pages & Jump Functionality (Extended from Project 1)
  - Updated the Look and Feel and README.md for P2.
