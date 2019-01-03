# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product (
  ean                           bigserial not null,
  name                          varchar(255),
  description                   varchar(255),
  picture                       varchar(255),
  constraint pk_product primary key (ean)
);


# --- !Downs

drop table if exists product cascade;

