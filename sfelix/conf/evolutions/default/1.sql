# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product (
  ean                           varchar(255) not null,
  name                          varchar(255),
  description                   varchar(255),
  picture                       image,
  picture_path                  varchar(255),
  constraint pk_product primary key (ean)
);


# --- !Downs

drop table if exists product;

