create table location_type
(
    id integer not null,
    name varchar(255) not null
);

comment on table  location_type                      is 'Location type';
comment on column location_type.id                   is 'Location type id';
comment on column location_type.name                 is 'Location type name';


alter table location_type add constraint location_type_pk primary key ( id );
create sequence seq_location_type_id  start with 1 increment by 1 cache 1;



create table location
(
    id bigint not null,
    parent_id bigint not null,
    description varchar(255) not null,
    gps bytea not null,
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
create sequence seq_location_id  start with 1 increment by 1 cache 1;
