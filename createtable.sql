CREATE schema moviedb;
USE moviedb;

CREATE TABLE movies (
id varchar(10) not null,
title varchar(100) not null,
year integer not null,
director varchar(100) not null,
primary key (id)
);

CREATE TABLE stars (
id varchar(10) not null,
name varchar(100) not null,
birthYear integer,
primary key (id)
);

CREATE TABLE stars_in_movies (
starId varchar(10) not null,
movieId varchar(10) not null,
foreign key (starId) references stars(id),
foreign key (movieId) references movies(id),
primary key (starId, movieId)
);

CREATE TABLE genres (
id integer not null auto_increment,
name varchar(32) not null,
primary key(id)
);

CREATE TABLE genres_in_movies(
genreId integer not null,
movieId varchar(10) not null,
foreign key (genreId) references genres(id),
foreign key (movieId) references movies(id),
primary key (genreId, movieId)
);

CREATE TABLE creditcards(
id varchar(20) not null,
firstName varchar(50) not null,
lastName varchar(50) not null,
expiration date not null,
primary key(id)
);

CREATE TABLE customers(
id integer not null auto_increment,
firstName varchar(50) not null,
lastName varchar(50) not null,
ccId varchar(20) not null,
address varchar(200) not null, 
email varchar(50) not null,
password varchar(20) not null,
primary key(id),
foreign key (ccId) references creditcards(id)
);

CREATE TABLE sales (
id integer not null auto_increment, 
customerId integer not null,
movieId varchar(10) not null, 
saleDate date not null,
primary key(id),
foreign key (customerId) references customers(id),
foreign key (movieId) references movies(id)
);

CREATE TABLE ratings (
movieId varchar(10) not null,
rating float not null,
numVotes integer not null,
foreign key (movieId) references movies(id)
);

CREATE TABLE employees (
email varchar(50),
password varchar(20) not null,
fullname varchar(100),
primary key(email)
);

