drop table if exists users;
drop table if exists user_board;
drop table if exists boards;
drop table if exists lists;
drop table if exists cards;

create table user (
    idUser serial primary key,
    email varchar(320) unique,
    name varchar(20),
    token varchar(36)
);

create table user_board (
    idUser Int,
    idBoard Int,
    primary key(idUser,idBoard)
);

create table board (
    idBoard serial primary key,
    name varchar(20) unique,
    description varchar(400)
);

create table list (
    idBoard Int,
    idList serial,
    name varchar(20),
    primary key (idBoard,idList),
    foreign key (idBoard) references board(idBoard)
);

create table card (
    idBoard Int,
    idList Int,
    idCard serial,
    startDate timestamp,
    endDate timestamp check ( endDate > card.startDate ),
    name varchar(20),
    description varchar(400),
    archived boolean,
    primary key (idBoard,idList,idCard),
    foreign key (idBoard,idList) references list(idBoard, idList)
);
