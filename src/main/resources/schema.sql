create schema graph;

alter schema graph owner to bigdata;

create table edges
(
  id     varchar                        not null
    constraint edges_pk
      primary key,
  source varchar                        not null,
  target varchar                        not null,
  weight double precision default 0.015 not null
);

alter table edges
  owner to bigdata;

create table nodes
(
  id    varchar          not null
    constraint nodes_pk
      primary key,
  name  varchar          not null,
  score double precision not null
);

alter table nodes
  owner to bigdata;

create unique index nodes_id_uindex
  on nodes (id);
