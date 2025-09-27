package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Was create student with ID: {}", student.getId());
        return studentRepository.save(student);
    }

    public Student getStudentId(Long id) {
        logger.info("Was invoked method for get student by id");
        logger.warn("Student with ID {} not found", id);
        return studentRepository.findById(id).get();
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student");
        logger.warn("Attempt to update a non-existent student with ID: {}", student.getId());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student by id");
        logger.debug("Student with ID {} successfully removed", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> filterAge(int age) {
        logger.info("Was invoked method for filtering students by age");
        logger.debug("Filtering students by age: {} years", age);
        return studentRepository.findAll().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for search students by age in the range");
        logger.debug("Search students in the age range: from {} to {} years", minAge, maxAge);
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getFacultyByStudent(Long id) {
        logger.info("Was invoked method for get faculty student by id");
        logger.debug("Get faculty student by ID: {}", id);
        return studentRepository.findById(id).map(Student::getFaculty).orElseThrow();
    }

    public Integer getCountAllStudents() {
        logger.info("Was invoked method for get count all student");
        logger.debug("Get total number of students");
        return studentRepository.getCountAllStudents();
    }

    public Double getAverageAgeStudents() {
        logger.info("Was invoked method for get average age students");
        logger.debug("Get average age students");
        return studentRepository.getAverageAgeStudents();
    }

    public Collection<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        logger.debug("Get last five students");
        return studentRepository.getLastFiveStudents();
    }

}