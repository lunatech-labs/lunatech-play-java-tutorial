# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product_entity (
  ean                           bigint auto_increment not null,
  name                          varchar(255),
  description                   varchar(255),
  url                           varchar(255),
  constraint pk_product_entity primary key (ean)
);


# --- !Downs

drop table if exists product_entity;

