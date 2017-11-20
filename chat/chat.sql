CREATE DATABASE chat;

create table chat_server_table (
msg_id		serial		primary key,
msg_date	timestamp with time zone not null default now(),
host_from	varchar(20),
port_from	int,
host_to		varchar(20),
port_to		int,
msg_data	varchar(40000)
);

create or replace function msg_send_by_trigger()
returns trigger
as $$
plpy.notice('TD["new"]={}'.format(TD["new"]))
host = TD["new"]["host_to"]
port = TD["new"]["port_to"]
msg = TD["new"]["msg_data"]
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
#sock.connect((host, port))
#sock.send(msg.encode('utf-8'))

#HOST = '192.168.219.154'
#PORT = 7770
ret = sock.sendto(msg.encode('utf-8'), (host, port))
plpy.notice('send len={} msg={}'.format(ret, msg))
$$ LANGUAGE plpythonu;

create trigger msg_send_trigger
after insert on chat_server_table
for each row
execute procedure msg_send_by_trigger();

INSERT INTO chat_server_table 
(msg_date, host_from, port_from, host_to, port_to, msg_data)
VALUES (now(), '192.168.219.154', 44099, '192.168.219.172', 56870, 'hello world!');

select * from chat_server_table;

CREATE TABLE id_address_table (
chat_id			varchar(20)		primary key,
host_ip			varchar(20),
port			int,
reg_date		timestamp with time zone not null default now(),
chat_state		varchar(10)
);

ALTER TABLE public.id_address_table
    ALTER COLUMN reg_date TYPE timestamptz ;
ALTER TABLE public.id_address_table
    ALTER COLUMN reg_date SET DEFAULT now();

ALTER TABLE public.id_address_table
    ALTER COLUMN reg_date SET NOT NULL;

ALTER TABLE public.chat_server_table
    ALTER COLUMN msg_date TYPE timestamp with time zone ;
ALTER TABLE public.chat_server_table
    ALTER COLUMN msg_date SET DEFAULT now();

ALTER TABLE public.chat_server_table
    ALTER COLUMN msg_date SET NOT NULL;

create table chat_message_table (
msg_id			serial		primary key,
chat_id			varchar(20),
msg_text		varchar(40000),
msg_date		timestamp with time zone not null default now()
);

create table buddy_conn_table (
chat_id			varchar(20)		primary key,
buddy_id		varchar(20),
conn_state		varchar(10),
conn_date		timestamp with time zone not null default now()
);

create or replace function msg_send2server_trigger()
returns trigger
as $$
plpy.notice('TD["new"]={}'.format(TD["new"]))
chat_id = TD["new"]["chat_id"]
msg = TD["new"]["msg_text"]
HOST = '192.168.219.172'
PORT = 7772
MAX_TIMEOUT = 10
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.connect((HOST, PORT))
sock.settimeout(MAX_TIMEOUT)

msg = '##send:'+chat_id+'##'+msg
ret = sock.send(msg.encode('utf-8'))
plpy.notice('send len={} msg={}'.format(ret, msg))
$$ LANGUAGE plpythonu;

create msg_send2server_trigger
after insert on chat_message_table
for each row
execute procedure msg_send2server_trigger();

