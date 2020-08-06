INSERT INTO post (id, title, body, category_id, is_public, stars, reference, user_id, created_at, edited_at, hashtags) VALUES(0, 'title', 'body', 0, 'false', 3, 'http://www.google.com', '12345', parsedatetime('01-01-2020 00:00:00', 'dd-MM-yyyy hh:mm:ss'), parsedatetime('01-01-2020 00:00:00', 'dd-MM-yyyy hh:mm:ss'), 't1,t2,t3') ;

INSERT INTO hashtag(hashtag, post_id) values('t1', 0) ;
INSERT INTO hashtag(hashtag, post_id) values('t2', 0) ;
INSERT INTO hashtag(hashtag, post_id) values('t3', 0) ;
