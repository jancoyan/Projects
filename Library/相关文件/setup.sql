drop table if exists t_book;

/*==============================================================*/
/* Table: t_book                                                */
/*==============================================================*/
create table t_book
(
    isbn                 varchar(20) not null,
    name                 varchar(50),
    author               varchar(50),
    price                decimal(10.2),
   instock              int(1),
   borrow_id            varchar(20),
   primary key (isbn)
);

drop table if exists t_user;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user
(
    id                   varchar(20),
    name                 varchar(40),
    type                 int(2),
    sex                  int(1),
    tel                  varchar(11),
    password             varchar(255)
);

insert into  t_user value('1', 'root', 0, 1, '0', '1');

drop table if exists t_user_log;

/*==============================================================*/
/* Table: t_user_log                                            */
/*==============================================================*/
create table t_user_log
(
    id                   varchar(20),
    isbn                 varchar(20),
    datetime             timestamp
);

drop table if exists t_booktype;

/*==============================================================*/
/* Table: t_booktype                                            */
/*==============================================================*/
create table t_booktype
(
    typeid               int(3) not null,
    typename             varchar(50),
    primary key (typeid)
);

insert into t_booktype VALUE(1, 'IT');
insert into t_booktype VALUE(2, '小说');
insert into t_booktype VALUE(3, '心理学');
insert into t_booktype VALUE(4, '古籍');
insert into t_booktype VALUE(5, '医学');
insert into t_booktype VALUE(6, '生活');
insert into t_booktype VALUE(7, '传记');

drop table if exists t_borrowing;

/*==============================================================*/
/* Table: t_borrowing                                           */
/*==============================================================*/
create table t_borrowing
(
    id                   varchar(20),
    isbn                 varchar(20),
    datetime             timestamp
);


drop table if exists t_operator_type;

/*==============================================================*/
/* Table: t_operator_type                                       */
/*==============================================================*/
create table t_operator_type
(
    typeid               int(1),
    typename             varchar(255)
);

insert into t_operator_type value (0, '借出');
insert into t_operator_type value (1, '归还');
