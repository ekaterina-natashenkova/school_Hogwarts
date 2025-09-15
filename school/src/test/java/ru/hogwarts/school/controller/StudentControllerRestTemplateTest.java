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
import java.util.Arrays;
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
    List<Student> addAndGetStudentList() {
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
        test.setName(name);
        test.setAge(age);
        return test;
    }

    private Faculty getTestFaculty(String name, String color) {
        Faculty test = new Faculty();
        test.setName(name);
        test.setColor(color);
        return test;
    }

    Student getTestStudentWithFaculty(String name, int age, Faculty faculty) {
        Student test = new Student();
        test.setName(name);
        test.setAge(age);
        test.setFaculty(faculty);
        return test;
    }

    @Test
    @DisplayName("Добавление студента")
    void createStudent() throws Exception {
        Student expected = getTestStudent("TestStudent", 20);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL(""),
                HttpMethod.POST,
                new HttpEntity<Student>(expected),
                new ParameterizedTypeReference<Student>() {
                }
        );

        assertThat(result).isNotNull();
        final Student actual = result.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @DisplayName("Просмотр добавленного студента по id")
    void getStudentId() throws Exception {
        List<Student> list = addAndGetStudentList();
        Student expected = getOneSomeStudents(list);
        String url = getURL("/") + expected.getId();

        ResponseEntity<Student> result = restTemplate.getForEntity(url, Student.class);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Обновление данных добавленного студента")
    void updateStudent() throws Exception {
        Student actual = studentRepository.save(getTestStudent("TestStudent", 20));
        Student expected = new Student();
        expected.setId(actual.getId());
        expected.setName("TestStudent1");
        expected.setAge(21);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL(""),
                HttpMethod.PUT,
                new HttpEntity<Student>(expected),
                Student.class // замена вызова new ParameterizedTypeReference<Student>() { }
        );

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getName()).isEqualTo("TestStudent1");
        assertThat(result.getBody().getAge()).isEqualTo(21);
        assertTrue(studentRepository.findAll().contains(expected));
        assertFalse(studentRepository.findAll().contains(actual));
    }

    @Test
    @DisplayName("Удаление добавленного студента по id")
    void deleteStudent() throws Exception {
        List<Student> repository = addAndGetStudentList();
        Student expected = getOneSomeStudents(repository);

        ResponseEntity<Student> result = restTemplate.exchange(
                getURL("/") + expected.getId(),
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
        int age = insecure().randomInt();
        Student test1 = studentRepository.save(getTestStudent("TestStudent1", age));
        Student test2 = studentRepository.save(getTestStudent("TestStudent2", age));
        List<Student> expected = Arrays.asList(test1, test2);

        ResponseEntity<Collection<Student>> result = restTemplate.exchange(
                getURL("/filterAge?age=" + age),
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
        int minAge = 20;
        int maxAge = 25;
        Student test1 = studentRepository.save(getTestStudent("TestStudent1", 21));
        Student test2 = studentRepository.save(getTestStudent("TestStudent2", 24));
        List<Student> expected = Arrays.asList(test1, test2);
        studentRepository.save(getTestStudent("TestStudent3", 30));

        /**
         * возможен вариант с безопасным формированием url
         * String url = UriComponentsBuilder.fromHttpUrl(getURL("/ageBetween"))
         *             .queryParam("minAge", minAge)
         *             .queryParam("maxAge", maxAge)
         *             .toUriString();
         */
        ResponseEntity<Collection<Student>> result = restTemplate.exchange(
                getURL("/ageBetween?minAge=" + minAge + "&maxAge=" + maxAge),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Вывод факультета заданного студента")
    void getFacultyByStudent() throws Exception {
        Faculty testFaculty = facultyRepository.save(getTestFaculty("TestFaculty", "TestColor"));
        Student testStudent = studentRepository.save(getTestStudentWithFaculty("TestStudent1", 20, testFaculty));

        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/getFacultyStudent/{id}"),
                HttpMethod.GET,
                null,
                Faculty.class,
                testStudent.getId()
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertEquals(testFaculty, result.getBody());
    }

}