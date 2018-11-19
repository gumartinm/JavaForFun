create schema if not exists application;

create table application.countries
(
    id serial not null,
    code varchar(80) not null,
    ftc_code varchar(80) null,
    hab_code varchar(80) null,
    name varchar(512) not null,
    geom geometry(MultiPolygon, 4326),
    modification_time timestamp
);

comment on table  application.countries                        is 'Countries';
comment on column application.countries.id                     is 'Countries id';
comment on column application.countries.code                   is 'Countries code';
comment on column application.countries.name                   is 'Countries name';
comment on column application.countries.geom                   is 'Countries geom';
comment on column application.countries.modification_time      is 'Countries modification time';

alter table application.countries add constraint application_countries_pk primary key (id);
alter table application.countries add constraint application_countries_unique_code unique (code);

create index application_countries_ind_01 on application.countries using GIST (geom);
