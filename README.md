- # General
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

