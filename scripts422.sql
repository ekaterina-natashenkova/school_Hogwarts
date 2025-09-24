-- Проектирование таблиц, настройка связи между таблицами и корректное определение типа данных

-- У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет)
CREATE TABLE Person (
id BIG SERIAL PRIMARY KEY,
name TEXT NOT NULL,
age INTEGER NOT NULL CHECK (age > 18),
driving_right BOOLEAN NOT NULL DEFAULT FALSE,
car_id BIGINT,
);

-- У каждой машины есть марка, модель и стоимость
CREATE TABLE Cars (
id BIG SERIAL PRIMARY KEY,
brand TEXT NOT NULL,
model TEXT NOT NULL,
price MONEY NOT NULL CHECK (price > 0),
);

-- У каждого человека есть машина, причем несколько человек могут пользоваться одной машиной
CREATE TABLE Person_Cars (
person_id BIG SERIAL,
car_id BIG SERIAL,
PRIMARY KEY (person_id, car_id),
FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE SET NULL,
FOREIGN KEY (car_id) REFERENCES car (id) ON DELETE SET NULL,
);