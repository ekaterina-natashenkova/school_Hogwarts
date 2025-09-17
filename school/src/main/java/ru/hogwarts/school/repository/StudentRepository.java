package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAgeBetween(int minAge, int maxAge);

    @Query(value = "SELECT count(*) as student from student", nativeQuery = true)
    Integer getCountAllStudents();

    @Query(value = "SELECT AVG(age) as age from student", nativeQuery = true)
    Double getAverageAgeStudents();

    @Query(value = "SELECT * from student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    Collection<Student> getLastFiveStudents();

}
