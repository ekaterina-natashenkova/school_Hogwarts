-- liquibase formatted sql

-- Индекс для поиска по названию и цвету факультета - Добавляем составной индекс (по name и color в faculty)

-- changeset kate:2
CREATE INDEX faculty_name_color_index ON faculty (name, color);