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

    private Student getTestStudent(String name, int age) {
        Student test = new Student();
        test.setName(name);
        test.setAge(age);
        return test;
    }

    private Student getTestStudent(Long id, String name, int age) {
        Student test = new Student();
        test.setId(id);
        test.setName(name);
        test.setAge(age);
        return test;
    }

    private Faculty getTestFaculty(Long id, String name, String color) {
        Faculty test = new Faculty();
        test.setId(id);
        test.setName(name);
        test.setColor(color);
        return test;
    }

    @Test
    @DisplayName("Добавление студента")
    void createStudent() throws Exception {
        Student testStudent = getTestStudent("TestStudent", 20);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", testStudent.getName());
        studentObject.put("age", testStudent.getAge());

        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Добавление студента - упрощенный вариант кода теста")
    void createStudentBasic() throws Exception {
        Student testStudent = getTestStudent("TestStudent", 20);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", testStudent.getName());
        studentObject.put("age", testStudent.getAge());

        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Просмотр добавленного студента по id")
    void getStudentId() throws Exception {
        Student testStudent = getTestStudent(10L, "TestStudent", 20);

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
        Student testStudent = getTestStudent(11L, "NewTestStudent", 21);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", testStudent.getName());
        studentObject.put("age", testStudent.getAge());

        when(studentService.updateStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student", testStudent.getId())
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudent.getId()))
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Удаление добавленного студента по id")
    void deleteStudent() throws Exception {
        Student testStudent = getTestStudent(10L, "TestStudent", 20);

        doNothing().when(studentService).deleteStudent(testStudent.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", testStudent.getId()))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(testStudent.getId());
    }

    @Test
    @DisplayName("Фильтрация студентов по возрасту")
    void filterAgeStudent() throws Exception {
        Student testStudent = getTestStudent(15L, "TestStudent", 23);
        int ageFilter = 23;

        Collection<Student> studentsByAge = Collections.singletonList(testStudent);

        when(studentService.filterAge(ageFilter)).thenReturn(studentsByAge);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filterAge?age={age}", ageFilter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testStudent.getId()))
                .andExpect(jsonPath("$[0].name").value(testStudent.getName()))
                .andExpect(jsonPath("$[0].age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Поиск студентов по возрасту в заданном диапазоне")
    void findByAgeBetween() throws Exception {
        Student testStudent = getTestStudent(15L, "TestStudent", 23);
        int minAge = 21;
        int maxAge = 25;

        Collection<Student> studentsByAge = Collections.singletonList(testStudent);

        when(studentService.findByAgeBetween(minAge, maxAge)).thenReturn(studentsByAge);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/ageBetween")
                        .param("minAge", String.valueOf(minAge))
                        .param("maxAge", String.valueOf(maxAge))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testStudent.getId()))
                .andExpect(jsonPath("$[0].name").value(testStudent.getName()))
                .andExpect(jsonPath("$[0].age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Вывод факультета заданного студента")
    void getFacultyByStudent() throws Exception {
        Faculty testFaculty = getTestFaculty(5L, "Lion", "orange");
        long studentId = 5L;

        when(studentService.getFacultyByStudent(studentId)).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/getFacultyStudent/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFaculty.getId()))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

}