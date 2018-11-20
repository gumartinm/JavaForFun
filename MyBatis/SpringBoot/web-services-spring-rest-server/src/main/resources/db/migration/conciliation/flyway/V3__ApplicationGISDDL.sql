create schema if not exists application;

create table application.countries
(
    id serial not null,
    code varchar(255) not null,
    name varchar(255) not null,
    geom geometry(point, 4326)
);

comment on table  application.countries                        is 'Countries';
comment on column application.countries.id                     is 'Countries id';
comment on column application.countries.code                   is 'Countries code';
comment on column application.countries.name                   is 'Countries name';
comment on column application.countries.geom                   is 'Countries geom';

alter table application.countries add constraint application_countries_pk primary key (id);
alter table application.countries add constraint application_countries_unique_code unique (code);

create index application_countries_ind_01 on application.countries using GIST (geom);
