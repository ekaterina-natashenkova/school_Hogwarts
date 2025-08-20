select * from student

-- Получить всех студентов, возраст (age) которых находится между MIN и MAX (нижняя граница должна быть меньше верхней)
select * from student where age > 15 and age < 25

-- Получить всех студентов, но отобразить только список их имен (name)
select name from student

-- Получить всех студентов, у которых в имени присутствует определенная буква (например А)
select * from student where name LIKE '%А%'

-- Получить всех студентов, у которых возраст (age) меньше идентификатора (id)
select * from student where age < id

-- Получить всех студентов упорядоченных по возрасту (age)
select * from student order by age