DELIMITER $$
create procedure add_movie(IN mdirector varchar(100), mid varchar(10), mtitle varchar(100), myear int, mstar varchar(100), mgenre varchar(32))
BEGIN
insert into movies(id,title,year,director) values(mid,mtitle,myear,mdirector);
if exists (select id from stars where upper(name) = upper(mstar)) then
	set @sid = (select id from stars where upper(name) = upper(mstar));
    select @sid;
	insert into stars_in_movies(starId,movieId) values(@sid,mid);
else 
	set @maxid = (select concat("nm",substring(max(id),3)+1) from stars);
    select @maxid;
    insert into stars(id,name,birthYear) values(@maxid,mstar,null);
    insert into stars_in_movies(starId,movieId) values(@maxid,mid);
end if;
if exists (select id from genres where upper(name) = upper(mgenre)) then
	set @gid = (select id from genres where upper(name) = upper(mgenre));
    select @gid;
	insert into genres_in_movies(genreId,movieId) values(@gid,mid);
else 
	set @maxgenreid = (select max(id)+1 from genres);
    insert into genres(id,name) values(@maxgenreid,mgenre);
    insert into genres_in_movies(genreId,movieId) values(@maxgenreid,mid);
    select @maxgenreid;
end if;
END
$$
DELIMITER ;
