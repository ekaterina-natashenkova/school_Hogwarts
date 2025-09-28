package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

/**
 * ResponseEntity.notFound().build() - когда ресурс не был найден, поскольку это соответствует коду состояния HTTP 404 Not Found. <br/>
 * ResponseEntity.status(HttpStatus.BAD_REQUEST).build() - когда запрос от клиента был неверным или не соответствует требованиям сервера.
 */

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student student1 = studentService.createStudent(student);
        return ResponseEntity.ok(student1);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentId(@PathVariable Long id) {
        Student student = studentService.getStudentId(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping()
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student student2 = studentService.updateStudent(student);
        if (student2 == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student2);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filterAge")
    public ResponseEntity<Collection<Student>> filterAgeStudent(@RequestParam int age) {
        if (age > 0){
            return ResponseEntity.ok(studentService.filterAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/ageBetween")
    public ResponseEntity<Collection<Student>> findByAgeBetween(@RequestParam int minAge, @RequestParam int maxAge) {
        return ResponseEntity.ok(studentService.findByAgeBetween(minAge, maxAge));
    }

    @GetMapping("/getFacultyStudent/{id}")
    public ResponseEntity<Faculty> getFacultyByStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getFacultyByStudent(id));
    }

    @GetMapping("/getCountAllStudents")
    public Integer getCountAllStudents() {
        return studentService.getCountAllStudents();
    }

    @GetMapping("/getAverageAgeStudents")
    public Double getAverageAgeStudents() {
        return studentService.getAverageAgeStudents();
    }

    @GetMapping("/getLastFiveStudents")
    public Collection<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/namesStartingWithA")
    public Collection<String> getStudentNamesStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/averageAgeAllStudents")
    public Double getAverageAgeAllStudents() {
        return studentService.getAverageAgeAllStudents();
    }

    @GetMapping("/print-parallel")
    public void printParallelAllStudentNames() {
        studentService.printParallelAllStudentNames();
    }

    @GetMapping("/print-synchronized")
    public void printSynchronizedAllStudentNames() {
        studentService.printSynchronizedAllStudentNames();
    }

}