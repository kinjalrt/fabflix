DELIMITER $$
create procedure add_movie(IN mdirector varchar(100), mid varchar(10), mtitle varchar(100), myear int, mstar varchar(100), mgenre varchar(32))
BEGIN
insert into movies(id,title,year,director) values(mid,mtitle,myear,mdirector);
if exists (select id from genres where upper(name) = upper(mgenre)) then
	set @gid = (select id from genres where upper(name) = upper(mgenre));
	insert into genres_in_movies(genreId,movieId) values(@gid,mid);
else 
	set @gid = (select max(id)+1 from genres);
    insert into genres(id,name) values(@gid,mgenre);
    insert into genres_in_movies(genreId,movieId) values(@gid,mid);
end if;
if exists (select id from stars where upper(name) = upper(mstar)) then
	set @sid = (select id from stars where upper(name) = upper(mstar));
	insert into stars_in_movies(starId,movieId) values(@sid,mid);
else 
	set @sid = (select concat("nm",substring(max(id),3)+1) from stars);
    insert into stars(id,name,birthYear) values(@sid,mstar,null);
    insert into stars_in_movies(starId,movieId) values(@sid,mid);
end if;
select @sid, @gid;
END
$$
DELIMITER ;

drop procedure add_movie;