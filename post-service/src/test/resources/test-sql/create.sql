    create table category (
       id bigint not null,
        description varchar(255),
        title varchar(255),
        primary key (id)
    )

        create table post (
       id bigint not null,
        body varchar(255),
        created_at timestamp not null,
        edited_at timestamp not null,
        hashtags varchar(255),
        is_public boolean not null,
        reference varchar(255) not null,
        stars integer not null check (stars<=5 AND stars>=0),
        title varchar(128),
        user_id varchar(255) not null,
        category_id bigint not null,
        primary key (id)
    )

        create sequence hibernate_sequence start with 1 increment by 1

    alter table post 
       add constraint FKg6l1ydp1pwkmyj166teiuov1b 
       foreign key (category_id) 
       references category


        create table hashtag (
       hashtag varchar(255) not null,
        post_id bigint not null,
        primary key (hashtag, post_id)
    )
