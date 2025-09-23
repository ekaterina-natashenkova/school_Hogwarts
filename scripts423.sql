-- JOIN-запрос для получения информацию обо всех студентах (имя и возраст) школы Хогвартс вместе с названиями факультетов
SELECT student.name, student.age, faculty.name FROM student
INNER JOIN faculty ON student.faculty_id = faculty.id;

-- JOIN-запрос для получения только тех студентов, у которых есть аватарки.
SELECT * FROM student
INNER JOIN avatar ON student.id = avatar.student_id;