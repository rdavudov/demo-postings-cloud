insert into users (username, password, enabled) values('user', '{noop}pass', true) ;
insert into authorities (username, authority) values ('user', 'HELLO') ; 

insert into users (username, password, enabled) values('friend', '{noop}pass', true) ;
insert into authorities (username, authority) values ('friend', 'HELLO') ; 
insert into authorities (username, authority) values ('friend', 'FRIEND') ; 
