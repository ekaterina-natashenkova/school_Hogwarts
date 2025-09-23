-- таблицы уже созданы в Spring Boot, поэтому не нужно создавать дополнительно таблицу в файле для sql-запросов
-- CREATE TABLE Student (id SERIAL, name TEXT, age INTEGER);
-- CREATE TABLE Faculty (id SERIAL, name TEXT, color TEXT);

-- Создание ограничений в имеющихся таблицах:
ALTER TABLE Student
-- Возраст студента не может быть меньше 16 лет
ADD CONSTRAINT age_constraint CHECK (age >= 16);
-- Имена студентов должны быть уникальными и не равны нулю
ALTER COLUMN name SET NOT NULL;
ADD CONSTRAINT name_unique UNIQUE (name);
--  При создании студента без возраста ему автоматически должно присваиваться 20 лет
ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE Faculty
-- Пара “значение названия” - “цвет факультета” должна быть уникальной
ADD CONSTRAINT name_color_unique UNIQUE (name, color);