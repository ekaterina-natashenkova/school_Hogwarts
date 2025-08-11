package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private long countStudent = 0;
    private final Map<Long, Student> students;
    private final StudentRepository studentRepository;

    public StudentService(long countStudent, Map<Long, Student> students, StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.countStudent++;
        this.students = new HashMap<>();
    }

    public long getCount() {
        return countStudent;
    }

    public Map<Long, Student> getStudents() {
        return students;
    }

    public Student createStudent(Student student) {
        countStudent++;
        students.put(student.getId(), student);
        return student;
    }

    public Student getStudentId(Long id) {
        return students.get(id);
    }

    public Student updateStudent(Student student) {
        if (!students.containsKey(student.getId())) {
            return null;
        }
        students.put(student.getId(), student);
        return student;
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }

    public Collection<Student> filterAge(int age) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());

    }

}