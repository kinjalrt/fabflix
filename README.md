# Project 3 - cs122b-spring20-team-80 
cs122b-spring20-team-80 created by GitHub Classroom
submitted by Kinjal Reetoo and Yasvi Patel on May 12th, 2020.

- reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing.

## Demo video URL

- [Project 3 - reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing](https://youtu.be/Xy-2g7unQCI)

- [Project 2 - Developing Fablix Website](https://www.youtube.com/watch?v=C_lelf4wlZE)

- [Project 1 - Set up AWS, MySQL, JDBC, Tomcat, Start Fablix](https://youtu.be/Cd_2F8tFhRM)

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

## All queries with Prepared Statements

Following files contain all queries with prepared statements.
  1. [SingleMovieServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/SingleMovieServlet.java)
  2. [SingleStarServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/SingleStarServlet.java)
  3. [SAXParserCast.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/SAXParserCast.java)
  4. [SAXParserMovies.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/SAXParserMovies.java)
  5. [SAXParserStars.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/SAXParserStars.java)
  6. [MovieListServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/MovieListServlet.java)
  7. [LoginServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/LoginServlet.java)
  8. [IndexServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/IndexServlet.java)
  9. [DashboardServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/DashboardServlet.java)
  10. [CheckoutServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/CheckoutServlet.java)
  11. [AdminLoginServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p3-api/src/AdminLoginServlet.java)
  
## Two parsing time optimization strategies compared with the naive approach.

- Use of batch:
    - In all 3 parsers we send all “insert into” statements in batches of 100 statements instead of executing each insert statement one after the other
    - This strategy considerably reduced the running time; for instance the naive approach took over 30 minutes to parse the casts xml file, after executing the insert statements in batches of 100 (or >=100 for the last batch only), the running time for the cast parser reduced by approximately half (~13/15min)

- Print inconsistencies to a file instead of console 
    - Using a buffered writer to speed up the process, instead of opening and closing the files on the disk too many times 
    - This reduces the running time to ~1/2 minutes depending on the file being parsed and the number of inconsistencies being reported for each 

- Limit the number of sql queries as much as possible; a few minor strategies were employed in order to try to limit the number of queries, including:
    - Since we insert data into the tables by batches of 100, when creating a new id, instead of retrieving the current maxId for each movie or star, we retrieve it once for the first movie/star in the batch and keep incrementing the id by +1 for the next 99 movies/stars in this batch.
    - Instead of checking for duplicates for genres_in_movies and stars_in_movies using “select” statements, we set both existing fields in both tables (genres_in_movies(genreId, movieId) and stars_in_movies(starId, movieId)) as primary keys. Therefore when the user tries to insert an already existing genreId-movieId or starId-movieId mapping in one of these tables, a MySQLException is thrown, from which we can get the error message with the associated values that caused the error.
  - In total, these query optimization techniques saved us ~1min, or less depending on the parsers. 

## Inconsistent data report from parsing/seperate file generated from code.

- Few assumptions:
  - Movies are differentiated by title AND year AND director
  - Movie should have at least 1 genre in order to be counted as consistent and be added to the database 
  - If a movie already exists in the database -> only add any missing genres 
  - If a genre is not reported in the movies documentation page provided (http://infolab.stanford.edu/pub/movies/doc.html#CATS) -> ignore the genre type
  - If a star is mapped to a movie that has a duplicate (based on title only) -> map star to either one of the duplicated movies since cast.xml does not include the year and director information related to the movie
- Inconsistency Reports
  - [ParserCast.txt](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/master/ParserCast.txt)
  - [ParserStars.txt](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/master/ParserStars.txt)
  - [ParserMovies.txt](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/master/ParserMovies.txt)
  
## Contribution

### Kinjal Reetoo 
  - Implemented Movie List and Single Star pages.
  - Initial git setup and wrote README.md for P1.
  - Implemented the Main Page with Search and Browse
  - Implemented the Shopping Cart
  - Demo video and final deployment for grading P2.
  - Added HTTPS
  - Used encrypted password
  - Importing large XML data files into the Fabflix database
  - Demo video and final deployement P3

### Yasvi Patel 
  - Implemented Single Movie page, hyperlinks and jump requirements.
  - Demo video and final deployment for grading P1.
  - Implemented the Login Page
  - Implement the Movie List Page, Single Pages & Jump Functionality (Extended from Project 1)
  - Updated the Look and Feel and README.md for P2.
  - Added reCAPTCHA
  - Used PreparedStatement
  - Implemented a Dashboard using Stored Procedure
  - Readme P3
