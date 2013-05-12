# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table local_instance (
  instance_id               varchar(255) not null,
  power_save                boolean,
  constraint pk_local_instance primary key (instance_id))
;

create table local_token (
  uuid                      varchar(255) not null,
  email                     varchar(255),
  created_at                timestamp,
  expire_at                 timestamp,
  is_sign_up                boolean,
  constraint pk_local_token primary key (uuid))
;

create table local_user (
  id                        varchar(255) not null,
  provider                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_local_user primary key (id))
;

create sequence local_instance_seq;

create sequence local_token_seq;

create sequence local_user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists local_instance;

drop table if exists local_token;

drop table if exists local_user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists local_instance_seq;

drop sequence if exists local_token_seq;

drop sequence if exists local_user_seq;

