CREATE OR REPLACE FUNCTION userinfo(
INOUT username name,
OUT user_id oid,
OUT is_superuser boolean)
AS $$
    u = plpy.execute("""
        select usename,usesysid,usesuper
        from pg_user
        where usename = '%s'""" % username)[0]
    return {'username':u['usename'], 'user_id':u['usesysid'], 'is_superuser':u['usesuper']}
$$ LANGUAGE plpythonu;

SELECT * FROM userinfo('postgres');

CREATE FUNCTION even_numbers_from_list(up_to int)
RETURNS SETOF int
AS $$
return range(0,up_to,2)
$$ LANGUAGE plpythonu;

SELECT * FROM even_numbers_from_list(10);

CREATE FUNCTION even_numbers_from_generator(up_to int)
RETURNS TABLE (even int, odd int)
AS $$
return ((i,i+1) for i in xrange(0,up_to,2))
$$ LANGUAGE plpythonu;

SELECT * FROM even_numbers_from_generator(10);

CREATE FUNCTION birthdates(OUT name text, OUT birthdate date)
RETURNS SETOF RECORD
AS $$
return (
{'name': 'bob', 'birthdate': '1980-10-10'},
{'name': 'mary', 'birthdate': '1983-02-17'},
['jill', '2010-01-15'],
)
$$ LANGUAGE plpythonu;

SELECT * FROM birthdates();

create or replace function send_message_udp(
host varchar,
port int,
msg varchar)
returns int
as $$
import socket
MAX_TIMEOUT = 5
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.connect((host, port))
sock.settimeout(MAX_TIMEOUT)

data = msg.encode('utf-8')
sock.send(data)
return len(data)
$$ LANGUAGE plpythonu;

select * from send_message_udp('192.168.219.154',7771,'hello world!');