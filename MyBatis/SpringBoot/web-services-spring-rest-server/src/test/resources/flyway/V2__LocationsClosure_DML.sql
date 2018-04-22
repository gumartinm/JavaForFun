insert INTO location (description, gps, parent_id) values
(nextval('seq_location_id'), 'Root', ST_GeographyFromText('SRID=4326;POINT(0.0 0.0)'), 1)
,(nextval('seq_location_id'), 'Spain', ST_GeographyFromText('SRID=4326;POINT(-3.7492 40.4637)'), 1)
,(nextval('seq_location_id'), 'Barcelona', ST_GeographyFromText('SRID=4326;POINT(2.1734 41.3851)'), 2)
,(nextval('seq_location_id'), 'Avila', ST_GeographyFromText('SRID=4326;POINT(-4.6812 40.6567)'), 2)
,(nextval('seq_location_id'), 'Toledo', ST_GeographyFromText('SRID=4326;POINT(-4.0273 39.8628)'), 2)
,(nextval('seq_location_id'), 'Sant Cugat', ST_GeographyFromText('SRID=4326;POINT(-2.0763 41.4526)'), 3)
,(nextval('seq_location_id'), 'Alcorcon', ST_GeographyFromText('SRID=4326;POINT(-3.8278 40.3468)'), 2)
,(nextval('seq_location_id'), 'Candeleda', ST_GeographyFromText('SRID=4326;POINT(-5.2436 40.1559)'), 4)
,(nextval('seq_location_id'), 'Arenas de San Pedro', ST_GeographyFromText('SRID=4326;POINT(-5.0833 40.2103)'), 4)
,(nextval('seq_location_id'), 'Home', ST_GeographyFromText('SRID=4326;POINT(2.073597 41.478646)'), 6)
,(nextval('seq_location_id'), 'Second Home', ST_GeographyFromText('SRID=4326;POINT(2.073597 41.478646)'), 8)
,(nextval('seq_location_id'), 'Portugal', ST_GeographyFromText('SRID=4326;POINT(-8.2245 39.3999)'), 1)
,(nextval('seq_location_id'), 'Oporto', ST_GeographyFromText('SRID=4326;POINT(-8.6291 41.1579)'), 12)
,(nextval('seq_location_id'), 'Lisboa', ST_GeographyFromText('SRID=4326;POINT(-9.1393 38.7223)'), 12)
,(nextval('seq_location_id'), 'Moreira', ST_GeographyFromText('SRID=4326;POINT(-8.654604 41.242565)'), 14);


insert INTO location_hierarchy (parent_id, child_id, level_id) values
('1', '1', '0')
,('2', '2', '0')
,('3', '3', '0')
,('4', '4', '0')
,('5', '5', '0')
,('6', '6', '0')
,('7', '7', '0')
,('8', '8', '0')
,('9', '9', '0')
,('10', '10', '0')
,('11', '11', '0')
,('12', '12', '0')
,('13', '13', '0')
,('14', '14', '0')
,('15', '15', '0');


-- Spain belongs to Root
-- Spain LOCATION: 2
-- Root LOCATION: 1
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 1
    AND CS.parent_id = 2;


-- Portugal belongs to Root
-- Portugal LOCATION: 12
-- Root LOCATION: 1
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 1
    AND CS.parent_id = 12;
    

-- Barcelona belongs to Spain
-- Barcelona LOCATION: 3
-- Spain LOCATION: 2
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 2
    AND CS.parent_id = 3;
    

-- Avila belongs to Spain
-- Avila LOCATION: 4
-- Spain LOCATION: 2
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 2
    AND CS.parent_id = 4;

    
-- Making Sant Cugat a child of Barcelona and grand child of Spain
-- Sant Cugat LOCATION: 6
-- Barcelona LOCATION: 3
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 3
    AND CS.parent_id = 6;
    
    
-- Making Candeleda a child of Avila and grand child of Spain
-- Candeleda LOCATION: 8
-- Avila LOCATION: 4
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 4
    AND CS.parent_id = 8;
    
    
-- Making Home a child of Sant Cugat and grand child of Barcelona and grand grand child of Spain
-- Sant Cugat LOCATION: 6
-- Home LOCATION: 10
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 6
    AND CS.parent_id = 10;
    

-- Making Second Home a child of Candeleda and grand child of Avila and grand grand child of Spain
-- Candeleda LOCATION: 8
-- Second Home LOCATION: 11
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 8
    AND CS.parent_id = 11;


-- Oporto belongs to Portugal
-- Oporto LOCATION: 13
-- Portugal LOCATION: 12
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 12
    AND CS.parent_id = 13;
    

-- Lisboa belongs to Portugal
-- Lisboa LOCATION: 14
-- Portugal LOCATION: 12
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 12
    AND CS.parent_id = 14;


-- Making Moreira a child of Oporto and grand child of Portugal
-- Moreria LOCATION: 15
-- Oporto LOCATION: 13
insert INTO location_hierarchy (parent_id, child_id, level_id)
select
    CF.parent_id,
    CS.child_id,
    (CF.level_id + (CS.level_id + 1))
FROM location_hierarchy CF, location_hierarchy CS
WHERE
    CF.child_id = 13
    AND CS.parent_id = 15;
