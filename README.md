- # Project 5 details:
    - #### Team#: 80
    
    - #### Names: Kinjal Reetoo, Yasvi Patel
    
    - #### Project 5 Video Demo Link: 
        - [Demo]()

    - #### Instruction of deployment:
        Followed the exact same process of deployment as described on Canvas.

        1) Ssh into AWS and run MySQL and Tomcat.
        2) Git clone git repository to AWS instance.
        `git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80.git`
        3) Build war file inside project repository.
        ```
        cd /home/ubuntu/cs122b-spring20-team-80/WebApp
        mvn clean package
        ```
        4) Copy newly built war file to Tomcat webapps folder.
        `cp ./target/*.war /home/ubuntu/tomcat/webapps`
        5) Launch the app from the Tomcat manager page on localhost.

    - #### Collaborations and Work Distribution:
        


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        Creates a pool of 100 connections to the backend sql. When executing a mySql query, one of the connections from the pool is fetched and used, and then put back in the pool for reuse for the other queries. MaxIdle is set to 30, meaning that if we have more than 30 free connections that are not being used, the tomcat server will automatically free some connections.
    
    - #### Explain how Connection Pooling works with two backend SQL.
        Each backend SQL, master and slave, both have their own pool of 100 connections. When they make a request, they both fetch a connection from their own pool and then put it back. 
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
        **Note:** The following files are on a seperate branch p4 which contains the scaled-version of the WebApp
        - [DashboardServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p4/WebApp/src/DashboardServlet.java)
        - [CheckoutServlet.java](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p4/WebApp/src/CheckoutServlet.java)
        - [context.xml](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80/blob/p4/WebApp/web/META-INF/context.xml)
    - #### How read/write requests were routed to Master/Slave SQL?
        Two data sources are created in the project's context.xml file. One for read: **moviedb** and the other for write: **moviedbMaster**. The **moviedb** data source connects to the 3306 port of the localhost. This allows the load balancer to randomly assign the query to either master or slave while maintaining sticky sessions. The **moviedbMaster** data source explicitly sends the write query to the master's IP. The updated database is reflected in both the master and slave.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    
        `cd /home/ubuntu`
        `javac script.java`
        `java script`
        
        Result will print to console Tj and Ts for the log.txt file located at /home/ubuntu/tomcat/webapps/cs122b-spring20-team-80 on each aws instance.


- # JMeter TS/TJ Time Measurement Report

(Note: After restarting my laptop and not using it for 12 hours, the Tj, Ts, and Tq times displayed by Jmeter  dropped from ~1000-1600ms to ~<=100-200ms. Every time I re-run a test-plan, Ts, Tj, and Tq seem to get slightly, gradually higher: the test plans run very fast (~100ms) after I let my laptop rest for some time, but if I use Jmeter for too long, the test plans slow down and averages in the ~1000ms. I could not figure out the reason why, I have disabled login filters for most of the test plans, and I am quite confident that I configured Jmeter correctly...)





| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                           | ![](https://i.imgur.com/1q0YlR0.png)   | 40                         | 14.126508                                  |  2.718686                       |   Ts takes up only 1/3 of Tq, meaning that most of the time is spent establishing the connections client-server for requests and responses.       |
| Case 2: HTTP/10 threads                        | ![](https://i.imgur.com/kMXVc76.png)   | 98                         | 65.286044                                  | 14.442434                        | from case 1, we can infer that establishing connections takes constant time. Here Tq consist mostly of Ts, which makes sense since we now have 10 threads, or 10 different users sending requests to the servlet.           |
| Case 3: HTTPS/10 threads                       | ![](https://i.imgur.com/ACEltg7.png)   | 118                        |   62.781212                               | 13.94621                        |  Tq, the time from when the client sends the request to when it receives it seems to be very slightly longer in https, which would make sense since https has to do the work http does and more. Once the connection is established however, the servlet and jdbc time are almost the same as case 2.         | 
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://i.imgur.com/jrzrplQ.png/)   | 515                         | 429.153758                                  | 34.74244                        | No pooling slows down the search considerably. This test plan shows how expensive it is to create a new connection to the database for every single query.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](https://i.imgur.com/0Q6UzBO.png)   | 92                         | 14.249849                                 | 2.768061                       | 2 backend sql does not seem to considerably slow down or increease tq, ts and tj.           |
| Case 2: HTTP/10 threads                        | ![](https://i.imgur.com/9xfqZHU.png)   | 84                         | 39.935207                                  |      8.050047                   | With 10 threads there is a clear improvement from the single instance version. Ts has improved by 1/3 while Tj has improved by almost 1/2. Tq is also slightly better. For both single and scaled versions the same, constant anount of time (~40ms) is spent outside of ts and tj, establishing connections.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |



**Bigger graph screenshots:**

**- Single-instance:**

Case 1: ![](https://i.imgur.com/1q0YlR0.png)

Case 2: ![](https://i.imgur.com/kMXVc76.png)

Case 3: ![](https://i.imgur.com/ACEltg7.png)

Case 4: ![](https://i.imgur.com/jrzrplQ.png)





**- Scaled version:**

Case 1: ![](https://i.imgur.com/0Q6UzBO.png)

Case 2: ![](https://i.imgur.com/9xfqZHU.png)

Case 3: 





# Project 4 details:
cs122b-spring20-team-80 created by GitHub Classroom
submitted by Kinjal Reetoo and Yasvi Patel on May 12th, 2020.

- Full Text Search, Autocomplete, Android Application, Fuzzy Search.

## Demo video URL

- [Project 4 - Full Text Search, Autocomplete, Android Application, Fuzzy Search](https://youtu.be/OO4qpw2fhHk)

- [Project 3 - reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing](https://youtu.be/Xy-2g7unQCI)

- [Project 2 - Developing Fablix Website](https://www.youtube.com/watch?v=C_lelf4wlZE)

- [Project 1 - Set up AWS, MySQL, JDBC, Tomcat, Start Fablix](https://youtu.be/Cd_2F8tFhRM)


## Instruction of deployment

Followed the exact same process of deployment as described on Canvas. 

  1. Ssh into AWS and run MySQL and Tomcat.
  2. Git clone git repository to AWS instance.
  
```bash
git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-80.git
```
  3. Build war file inside project repository.
  
```bash
cd /home/ubuntu/cs122b-spring20-team-80/WebApp
mvn clean package
```
  4. Copy newly built war file to Tomcat webapps folder.
```bash
cp ./target/*.war /home/ubuntu/tomcat/webapps
```
  5. Launch the app from the Tomcat manager page on localhost.
  
  
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
  - Improving the Fabflix by full-text Search and Autocomplete
  - Demo video and Readme P4

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
  - Developing an Android App for Fabflix
  



# Project 3 details:

## Substring matching design

We used the mysql command  LIKE "%ABC%"  for the movie title, director name and star name search parameters; this command is case insensitive and finds any string that contains the pattern ABC anywhere in the string. For the year parameter we used exact match.

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
  

