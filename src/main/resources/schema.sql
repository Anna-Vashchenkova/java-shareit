drop table if exists users cascade;
create table if not exists users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) not null,
    email varchar(25) not null,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

drop table if exists requests cascade;
create table if not exists requests
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(200) not null,
    requestor_id BIGINT not null,
    created_time TIMESTAMP WITHOUT TIME ZONE,
    foreign key (requestor_id) references users (id) on delete cascade
    );

drop table if exists items cascade;
create table if not exists items
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) not null,
    description varchar(200) not null,
    is_available varchar(11) not null,
    owner_id BIGINT not null,
    request_id BIGINT not null,
    foreign key (owner_id) references users (id) on delete cascade,
    foreign key (request_id) references requests (id) on delete cascade
    );