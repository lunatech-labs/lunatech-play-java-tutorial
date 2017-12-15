# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product (
  id                            bigint not null,
  ean                           varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  picture                       Image,
  path_local_picture            varchar(255),
  constraint uq_product_ean unique (ean),
  constraint pk_product primary key (id)
);
create sequence product_seq;

create table url_entity (
  url_entity_number             bigint not null,
  url                           varchar(1024),
  constraint pk_url_entity primary key (url_entity_number)
);
create sequence URL_ENTITY_seq;

create table url_entity_web_search (
  url_entity_url_entity_number  bigint not null,
  web_search_id                 bigint not null,
  constraint pk_url_entity_web_search primary key (url_entity_url_entity_number,web_search_id)
);

create table web_search (
  id                            bigint not null,
  name                          varchar(1024),
  constraint pk_web_search primary key (id)
);
create sequence WEB_SEARCH_seq;

create table web_search_url_entity (
  web_search_id                 bigint not null,
  url_entity_url_entity_number  bigint not null,
  constraint pk_web_search_url_entity primary key (web_search_id,url_entity_url_entity_number)
);

alter table url_entity_web_search add constraint fk_url_entity_web_search_url_entity foreign key (url_entity_url_entity_number) references url_entity (url_entity_number) on delete restrict on update restrict;
create index ix_url_entity_web_search_url_entity on url_entity_web_search (url_entity_url_entity_number);

alter table url_entity_web_search add constraint fk_url_entity_web_search_web_search foreign key (web_search_id) references web_search (id) on delete restrict on update restrict;
create index ix_url_entity_web_search_web_search on url_entity_web_search (web_search_id);

alter table web_search_url_entity add constraint fk_web_search_url_entity_web_search foreign key (web_search_id) references web_search (id) on delete restrict on update restrict;
create index ix_web_search_url_entity_web_search on web_search_url_entity (web_search_id);

alter table web_search_url_entity add constraint fk_web_search_url_entity_url_entity foreign key (url_entity_url_entity_number) references url_entity (url_entity_number) on delete restrict on update restrict;
create index ix_web_search_url_entity_url_entity on web_search_url_entity (url_entity_url_entity_number);


# --- !Downs

alter table url_entity_web_search drop constraint if exists fk_url_entity_web_search_url_entity;
drop index if exists ix_url_entity_web_search_url_entity;

alter table url_entity_web_search drop constraint if exists fk_url_entity_web_search_web_search;
drop index if exists ix_url_entity_web_search_web_search;

alter table web_search_url_entity drop constraint if exists fk_web_search_url_entity_web_search;
drop index if exists ix_web_search_url_entity_web_search;

alter table web_search_url_entity drop constraint if exists fk_web_search_url_entity_url_entity;
drop index if exists ix_web_search_url_entity_url_entity;

drop table if exists product;
drop sequence if exists product_seq;

drop table if exists url_entity;
drop sequence if exists URL_ENTITY_seq;

drop table if exists url_entity_web_search;

drop table if exists web_search;
drop sequence if exists WEB_SEARCH_seq;

drop table if exists web_search_url_entity;

