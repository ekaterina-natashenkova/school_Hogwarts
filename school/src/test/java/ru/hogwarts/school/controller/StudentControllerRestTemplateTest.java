package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.insecure;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void beforeEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    /**
     * Метод для динамического формирования полного URL адреса к эндпоинтам
     */
    String getURL(String url) {
        return "http://localhost:" + port + "/student" + url;
    }

    /**
     * Метод для формирования списка студентов
     */
    List<Student> getStudentRepository() {
        return Stream.generate(() -> studentRepository.save(new Student()))
                .limit(nextInt(5, 10))
                .toList();
    }

    /**
     * Метод для выбора случайного студента из списка
     */
    Student getOneSomeStudents(List<Student> repository) {
        return repository.get(new Random().nextInt(repository.size()));
    }

    Student getTestStudent(String name, int age) {
        Student test = new Student();
        test.setId(0L);
        test.setName(name);
        test.setAge(age);
        return test;
    }

    Student getTestStudentWithFaculty(String name, int age, Faculty faculty) {
        Student test = new Student();
        test.setId(0L);
        test.setName(name);
        test.setAge(age);
        test.setFaculty(faculty);
        return test;
    }

    @Test
    @DisplayName("Добавление студента")
    void createStudent() throws Exception {
        List<Student> repository = getStudentRepository();
        Student expected = getTestStudent("TestStudent", 20);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL("/student"),
                HttpMethod.POST,
                new HttpEntity<Student>(expected),
                new ParameterizedTypeReference<Student>() {
                }
        );
        expected.setId(result.getBody().getId());

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Просмотр добавленного студента по id")
    void getStudentId() throws Exception {
        List<Student> repository = getStudentRepository();
        Student expected = getOneSomeStudents(repository);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL("/student/{id}") + expected.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Student>() {
                }
        );

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Обновление данных добавленного студента")
    void updateStudent() throws Exception {
        List<Student> repository = getStudentRepository();
        Student actual = getOneSomeStudents(repository);
        Student expected = new Student();
        expected.setId(actual.getId());
        expected.setName("TestStudent");
        expected.setAge(200);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL("/student"),
                HttpMethod.PUT,
                new HttpEntity<Student>(expected),
                new ParameterizedTypeReference<Student>() {
                }
        );

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
        assertTrue(studentRepository.findAll().contains(expected));
        assertFalse(studentRepository.findAll().contains(actual));
    }

    @Test
    @DisplayName("Удаление добавленного студента по id")
    void deleteStudent() throws Exception {
        List<Student> repository = getStudentRepository();
        Student expected = getOneSomeStudents(repository);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL("/student/{id}") + expected.getId(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Student>() {
                }
        );

        assertFalse(studentRepository.findAll().contains(expected));
        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
    }

    @Test
    @DisplayName("Фильтрация студентов по возрасту")
    void filterAgeStudent() throws Exception {
        List<Student> repository = getStudentRepository();
        int age = insecure().randomInt();
        Student test1 = getTestStudent("TestStudent1", age);
        Student test2 = getTestStudent("TestStudent2", age);
        studentRepository.save(test1);
        studentRepository.save(test2);
        List<Student> expected = new ArrayList<>();
        expected.add(test1);
        expected.add(test2);

        ResponseEntity<Collection<Student>> result = restTemplate.exchange(
                getURL("/student/filterAge" + age),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertThat(result.getBody())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Поиск студентов по возрасту в заданном диапазоне")
    void findByAgeBetween() throws Exception {
        List<Student> repository = getStudentRepository();
        int age = insecure().randomInt();
        Student test1 = getTestStudent("TestStudent1", insecure().randomInt(age - 1, age + 5));
        Student test2 = getTestStudent("TestStudent2", insecure().randomInt(age - 1, age + 5));
        studentRepository.save(test1);
        studentRepository.save(test2);
        List<Student> expected = new ArrayList<>();
        expected.add(test1);
        expected.add(test2);

        ResponseEntity<Collection<Student>> result = restTemplate.exchange(
                getURL("/student/ageBetween" + "&min=" + (age - 1) + "&max=" + (age + 5)),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Поиск студентов по возрасту в заданном диапазоне")
    void getFacultyByStudent() throws Exception {
        List<Student> repository = getStudentRepository();
        Faculty testFaculty = new Faculty();
        facultyRepository.save(testFaculty);
        Student test1 = getTestStudentWithFaculty("TestStudent1", 20, testFaculty);
        Student test2 = getTestStudentWithFaculty("TestStudent2", 25, testFaculty);
        studentRepository.save(test1);
        studentRepository.save(test2);
        List<Student> expected = new ArrayList<>();
        expected.add(test1);
        expected.add(test2);
        testFaculty.setStudents(expected);
        facultyRepository.save(testFaculty);

        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/student/getFacultyStudent/{id}" + test1.getId()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Faculty>() {
                }
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertEquals(testFaculty, result.getBody());
    }

}