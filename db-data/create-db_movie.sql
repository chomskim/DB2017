create schema movie;
use movie;

create table tmdb-movies (
id				int			primary key,
title			varchar(100),
genres			varchar(1000),
original_title	varchar(100),
popularity		double,
release_date	date,
budget			decimal(15),
revenue			decimal(15),
runtime			int,
vote_average	double,
vote_count		int
);

load data local infile '~/db-data/tmdb-movies.csv' into table tmdb-movies 
fields terminated by ',' enclosed by '"' 
# ignore 1 lines
;

create table rating (
user_id			int not null,
movie_id		int not null,
rating			int,
time_stamp		bigint,
primary key (user_id, movie_id)
);
load data local infile '~/db-data/rating.csv' into table rating fields terminated by ',';

create table links (
movie_id		int not null	primary key,
imdb_id			int,
tmdb_id			int
);
load data local infile '~/db-data/links.csv' into table links 
fields terminated by ',' lines terminated by '\n' ignore 1 lines
(movie_id, imdb_id, @vtmdb_id)
set tmdb_id = nullif(TRIM(@vtmdb_id),'');

create table movies (
id				int			primary key,
title			varchar(200),
genres			varchar(200)
);
load data local infile '~/db-data/movies.csv' into table movies 
fields terminated by ',' 
enclosed by '"'
lines terminated by '\n' 
ignore 1 lines;

create table users (
user_id			int not null	primary key,
age				int,
gender			char(1),
occupation		varchar(50),
zip_code		varchar(20)
);
load data local infile '~/db-data/users.csv' into table users 
fields terminated by '|' 
lines terminated by '\n' 
ignore 1 lines;

