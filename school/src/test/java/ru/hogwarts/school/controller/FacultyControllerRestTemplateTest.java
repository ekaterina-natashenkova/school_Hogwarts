package ru.hogwarts.school.controller;

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

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTemplateTest {

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
    private String getURL(String url) {
        return "http://localhost:" + port + "/faculty" + url;
    }

    /**
     * Метод для формирования списка факультетов
     */
    private List<Faculty> addAndGetFacultyList() {
        return Stream.generate(() -> facultyRepository.save(new Faculty()))
                .limit(nextInt(1, 5))
                .toList();
    }

    private Faculty getOneSomeFaculty(List<Faculty> repository) {
        return repository.get(new Random().nextInt(repository.size()));
    }

    private Faculty getTestFaculty(String name, String color) {
        Faculty test = new Faculty();
        test.setName(name);
        test.setColor(color);
        return test;
    }

    private Student getTestStudentWithFaculty(String name, int age, Faculty faculty) {
        Student test = new Student();
        test.setName(name);
        test.setAge(age);
        test.setFaculty(faculty);
        return test;
    }

    @Test
    @DisplayName("Добавление факультета")
    void createFaculty() {
        Faculty expected = getTestFaculty("TestFaculty", "Color");

        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/add"),
                HttpMethod.POST,
                new HttpEntity<Faculty>(expected),
                new ParameterizedTypeReference<Faculty>() {
                }
        );

        assertThat(result).isNotNull();
        final Faculty actual = result.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @DisplayName("Просмотр добавленного факультета по id")
    void getFacultyId() {
        List<Faculty> repository = addAndGetFacultyList();
        Faculty expected = getOneSomeFaculty(repository);
        String url = getURL("/") + expected.getId();
        //System.out.println(url);
        ResponseEntity<Faculty> result = restTemplate.getForEntity(url, Faculty.class);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        //System.out.println(expected);
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Обновление данных факультета")
    void updateFaculty() {
        List<Faculty> repository = addAndGetFacultyList();
        Faculty actual = getOneSomeFaculty(repository);
        Faculty expected = new Faculty();
        expected.setId(actual.getId());
        expected.setName("TestFaculty");
        expected.setColor("Color");


        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/"),
                HttpMethod.PUT,
                new HttpEntity<Faculty>(expected),
                new ParameterizedTypeReference<Faculty>() {
                }
        );

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
        assertTrue(facultyRepository.findAll().contains(expected));
        assertFalse(facultyRepository.findAll().contains(actual));
    }

    @Test
    @DisplayName("Удаление добавленного факультета по id")
    void deleteFaculty() {
        List<Faculty> repository = addAndGetFacultyList();
        Faculty expected = getOneSomeFaculty(repository);

        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/") + expected.getId(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Faculty>() {
                }
        );

        assertFalse(facultyRepository.findAll().contains(expected));
        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
    }

    @Test
    @DisplayName("Фильтрация факультетов по цвету")
    void filterColorFaculty() {
        List<Faculty> repository = addAndGetFacultyList();
        String color = "red";
        Faculty test1 = getTestFaculty("TestFaculty1", color);
        Faculty test2 = getTestFaculty("TestFaculty2", color);
        facultyRepository.save(test1);
        facultyRepository.save(test2);
        List<Faculty> expected = new ArrayList<>();
        expected.add(test1);
        expected.add(test2);

        ResponseEntity<Collection<Faculty>> result = restTemplate.exchange(
                getURL("/filterColor" + color),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
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
    @DisplayName("Поиск факультетов по названию или цвету")
    void findByNameOrColor() {
        List<Faculty> repository = addAndGetFacultyList();
        String name = "Lion";
        String color = "orange";
        Faculty expected1 = getTestFaculty(name, "color");
        Faculty expected2 = getTestFaculty("name", color);
        facultyRepository.save(expected1);
        facultyRepository.save(expected2);
        List<Faculty> expected = new ArrayList<>();
        expected.add(expected1);
        expected.add(expected1);

        ResponseEntity<Faculty> result = restTemplate.exchange(
                getURL("/nameOrColor" + "&name=" + name.toUpperCase() + "&color=" + color.toUpperCase()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Faculty>() {
                }
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected, result.getBody());
    }

    @Test
    @DisplayName("Получение списка студентов заданного факультета")
    void getStudentsByFaculty() {
        List<Faculty> repository = addAndGetFacultyList();
        Faculty testFaculty = getOneSomeFaculty(repository);
        Student test1 = getTestStudentWithFaculty("Dag", 22, testFaculty);
        Student test2 = getTestStudentWithFaculty("Bony", 24, testFaculty);
        studentRepository.save(test1);
        studentRepository.save(test2);
        List<Student> expected = new ArrayList<>();
        expected.add(test1);
        expected.add(test2);
        testFaculty.setStudents(expected);
        facultyRepository.save(testFaculty);

        ResponseEntity<Collection<Student>> result = restTemplate.exchange(
                getURL("/getStudentsFaculty/" + testFaculty.getId()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        assertEquals(HttpStatus.valueOf(200), result.getStatusCode());
        assertThat(result).isNotNull();
        assertEquals(expected, result.getBody());
    }

}