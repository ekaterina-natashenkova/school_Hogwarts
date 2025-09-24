-- liquibase formatted sql

-- Индекс для поиска по имени студента - Добавляем индекс на поле name в таблице student

-- changeset kate:1
CREATE INDEX student_name_index ON student (name);