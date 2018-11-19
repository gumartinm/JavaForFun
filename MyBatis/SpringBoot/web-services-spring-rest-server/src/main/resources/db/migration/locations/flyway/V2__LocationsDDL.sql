-- When using postgis, postgis functions must be created in the same schema as the
-- one that we want to use. Otherwise, you will not be able to use postgis :(
-- So, if you want to work on some schema that is not the 'public' one and use
-- in your jdbc connection the option '?defaultschema=locations' you will need to
-- create the postgis extensions in the schema where you want to work :(
-- create schema if not exists locations;


create table location_type
(
    id integer not null,
    name varchar(255) not null
);

comment on table  location_type                      is 'Location type';
comment on column location_type.id                   is 'Location type id';
comment on column location_type.name                 is 'Location type name';


alter table location_type add constraint location_type_pk primary key ( id );
create sequence seq_location_type_id as integer start with 1 increment by 1 cache 1;



create table location
(
    id bigint not null,
    parent_id bigint not null,
    description varchar(255) not null,
    gps geometry(point, 4326),
    location_type_id integer not null
);

comment on table  location                          is 'Locations';
comment on column location.id                       is 'Location id';
comment on column location.parent_id                is 'Location parent id';
comment on column location.description              is 'Location  name';
comment on column location.gps                      is 'Location geography type';

alter table location add constraint location_pk primary key ( id );
alter table location add constraint location_fk_01 foreign key ( parent_id ) references location ( id ) on delete no action on update no action;
alter table location add constraint location_fk_02 foreign key ( location_type_id ) references location_type ( id ) on delete no action on update no action;
create index location_ind_01 on location using GIST (gps);
create sequence seq_location_id as bigint start with 1 increment by 1 cache 1;
