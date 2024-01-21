create table if not exists warehouse
(
    id                 int auto_increment primary key,
    name               varchar(50)   not null,
    address_line_1     varchar(100)  not null,
    address_line_2     varchar(100)  null,
    city               varchar(50)   not null,
    state              varchar(50)   not null,
    country            varchar(50)   not null,
    inventory_quantity int default 0 not null
);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main1', 'SomeStreet 2', '', 'Augusta', 'Maine', 'USA', 30);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main2', 'SomeStreet 6', '', 'Augusta', 'Maine', 'USA', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main3', 'Orlova 5', '', 'Augusta', 'Maine', 'USA', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main4', 'SomeStreet 24', '', 'Augusta', 'Maine', 'USA', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main5', 'SomeStreet 23', '', 'Austin', 'Texas', 'USA', 222);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main6', 'ForestStreet 24', '', 'Augusta', 'Maine', 'USA', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main7', 'SomeStreet 40', '', 'Augusta', 'Maine', 'USA', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main8', 'SomeStreet 41', '', 'Rivne', 'Rivnenska', 'Ukraine', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main9', 'SomeStreet 42', '', 'Kiev', 'Kievska', 'Ukraine', 300);

INSERT INTO colossus.warehouse (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
VALUES ('Main10', 'SomeStreet 66', '', 'Augusta', 'Maine', 'USA', 300);
