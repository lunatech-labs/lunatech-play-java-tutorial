# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product (
  id                            bigint not null,
  ean                           varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  picture                       blob,
  constraint pk_product primary key (id)
);
create sequence product_seq;


# --- !Downs

drop table if exists product;
drop sequence if exists product_seq;

