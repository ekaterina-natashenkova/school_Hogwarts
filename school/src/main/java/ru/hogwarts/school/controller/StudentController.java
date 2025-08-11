package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

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
            return ResponseEntity.notFound().build(); // ?? или тут лучше использовать ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping()
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student student2 = studentService.updateStudent(student);
        if (student2 == null) {
            return ResponseEntity.notFound().build(); // ?? или тут лучше использовать ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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

}