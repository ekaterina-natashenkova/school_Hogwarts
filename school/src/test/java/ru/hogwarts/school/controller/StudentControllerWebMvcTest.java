package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private StudentService studentService;

    Student getTestStudent(String name, int age) {
        Student test = new Student();
        test.setName(name);
        test.setAge(age);
        return test;
    }

    Student getTestStudent(Long id, String name, int age) {
        Student test = new Student();
        test.setId(id);
        test.setName(name);
        test.setAge(age);
        return test;
    }

    Faculty getTestFaculty(Long id, String name, String color) {
        Faculty test = new Faculty();
        test.setId(id);
        test.setName(name);
        test.setColor(color);
        return test;
    }

    @Test
    @DisplayName("Добавление студента")
    void createStudent() throws Exception {
        String name = "TestStudent";
        int age = 20;
        Student testStudent = getTestStudent(name, age);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("color", age);

        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    @DisplayName("Просмотр добавленного студента по id")
    void getStudentId() throws Exception {
        long id = 10L;
        String name = "TestStudent";
        int age = 20;
        Student testStudent = getTestStudent(id, name, age);

        when(studentService.getStudentId(anyLong())).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", testStudent.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudent.getId()))
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Обновление данных добавленного студента")
    void updateStudent() throws Exception {
        long id = 11L;
        String newName = "NewTestStudent";
        int newAge = 21;
        Student testStudent = getTestStudent(id, newName, newAge);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", newName);
        studentObject.put("age", newAge);

        when(studentService.updateStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student", id)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.age").value(newAge));
    }

    @Test
    @DisplayName("Удаление добавленного студента по id")
    void deleteStudent() throws Exception {
        long id = 10L;
        String name = "TestStudent";
        int age = 20;
        Student testStudent = getTestStudent(id, name, age);

        doNothing().when(studentService).deleteStudent(testStudent.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", testStudent.getId()))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(testStudent.getId());
    }

    @Test
    @DisplayName("Фильтрация студентов по возрасту")
    void filterAgeStudent() throws Exception {
        int ageFilter = 23;
        long id = 15L;
        String name = "TestStudent";
        int age = 23;
        Student testStudent = getTestStudent(id, name, age);

        Collection<Student> studentsByAge = Collections.singletonList(testStudent);

        when(studentService.filterAge(ageFilter)).thenReturn(studentsByAge);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filterAge", ageFilter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].age").value(age));
    }

    @Test
    @DisplayName("Поиск студентов по возрасту в заданном диапазоне")
    void findByAgeBetween() throws Exception {
        long id = 15L;
        String name = "TestStudent";
        int age = 23;
        Student testStudent = getTestStudent(id, name, age);
        int minAge = 21;
        int maxAge = 25;

        Collection<Student> studentsByAge = Collections.singletonList(testStudent);

        when(studentService.findByAgeBetween(minAge, maxAge)).thenReturn(studentsByAge);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/ageBetween")
                        .param("ageMin", String.valueOf(minAge))
                        .param("ageMax", String.valueOf(maxAge))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].age").value(age));
    }

    @Test
    @DisplayName("Вывод факультета заданного студента")
    void getFacultyByStudent() throws Exception {
        long studentId = 5L;
        long id = 5L;
        String name = "Lion";
        String color = "orange";
        Faculty testFaculty = getTestFaculty(id, name, color);

        when(studentService.getFacultyByStudent(studentId)).thenReturn((Collection<Student>) testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/getFacultyStudent/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

}