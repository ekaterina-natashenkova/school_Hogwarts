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

import java.util.Arrays;
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
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private StudentService studentService;

    Faculty getTestFaculty(String name, String color) {
        Faculty test = new Faculty();
        test.setName(name);
        test.setColor(color);
        return test;
    }

    Faculty getTestFaculty(Long id, String name, String color) {
        Faculty test = new Faculty();
        test.setId(id);
        test.setName(name);
        test.setColor(color);
        return test;
    }

    Student getTestStudent(Long id, String name, int age) {
        Student test = new Student();
        test.setId(id);
        test.setName(name);
        test.setAge(age);
        return test;
    }

    @Test
    @DisplayName("Добавление факультета")
    void createFaculty() throws Exception {
        String name = "TestFaculty";
        String color = "RandomColor";
        Faculty testFaculty = getTestFaculty(name, color);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", name);
        facultyObject.put("color", color);

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    @DisplayName("Просмотр добавленного факультета по id")
    void getFacultyId() throws Exception {
        long id = 10L;
        String name = "TestFaculty";
        String color = "RandomColor";
        Faculty testFaculty = getTestFaculty(id, name, color);

        when(facultyService.getFacultyId(anyLong())).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + testFaculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFaculty.getId()))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Обновление данных факультета")
    void updateFaculty() throws Exception {
        long id = 10L;
        String newName = "TestFaculty";
        String newColor = "RandomColor";
        Faculty testFaculty = getTestFaculty(id, newName, newColor);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", newName);
        facultyObject.put("color", newColor);

        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty", id)
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.color").value(newColor));
    }

    @Test
    @DisplayName("Удаление добавленного факультета по id")
    void deleteFaculty() throws Exception {
        long id = 10L;
        String name = "TestFaculty";
        String color = "RandomColor";
        Faculty testFaculty = getTestFaculty(id, name, color);

        doNothing().when(facultyService).deleteFaculty(testFaculty.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", testFaculty.getId()))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(testFaculty.getId());
    }

    @Test
    @DisplayName("Фильтрация факультетов по цвету")
    void filterColorFaculty() throws Exception {
        long id = 10L;
        String name = "TestFaculty";
        String color = "RandomColor";
        Faculty testFaculty = getTestFaculty(id, name, color);
        String colorFilter = "Color";

        Collection<Faculty> facultyByColor = Collections.singletonList(testFaculty);

        when(facultyService.filterColor(colorFilter)).thenReturn(facultyByColor);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filterAge", colorFilter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].color").value(color));
    }

    @Test
    @DisplayName("Поиск факультетов по названию или цвету")
    void findByNameOrColor() throws Exception {
        Faculty testFaculty1 = getTestFaculty(11L, "TestFaculty1", "RandomColor1");
        Faculty testFaculty2 = getTestFaculty(12L, "TestFaculty2", "RandomColor2");

        String findName = "TestFaculty1";
        String findColor = "RandomColor2";

        Collection<Faculty> findByName = Collections.singletonList(testFaculty1);
        Collection<Faculty> findByColor = Collections.singletonList(testFaculty2);

        when(facultyService.findByNameOrColor(findName)).thenReturn(findByName);
        when(facultyService.findByNameOrColor(findColor)).thenReturn(findByColor);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/nameOrColor")
                        .param("find", findName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testFaculty1.getId()))
                .andExpect(jsonPath("$[0].name").value("TestFaculty1"))
                .andExpect(jsonPath("$[0].color").value("RandomColor1"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/nameOrColor")
                        .param("find", findColor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12L))
                .andExpect(jsonPath("$[0].name").value("TestFaculty2"))
                .andExpect(jsonPath("$[0].color").value("RandomColor2"));
    }

    @Test
    @DisplayName("Получение списка студентов заданного факультета")
    void getStudentsByFaculty() throws Exception {
        Student testStudent1 = getTestStudent(11L, "TestStudent1", 24);
        Student testStudent2 = getTestStudent(12L, "TestStudent2", 26);
        long facultyId = 1L;

        Collection<Student> studentsByFaculty = Arrays.asList(testStudent1, testStudent2);

        when(facultyService.getStudentsByFaculty(facultyId)).thenReturn(studentsByFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/getStudentsFaculty/{id}", facultyId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11L))
                .andExpect(jsonPath("$[0].name").value("TestStudent1"))
                .andExpect(jsonPath("$[0].age").value(24))
                .andExpect(jsonPath("$[1].id").value(12L))
                .andExpect(jsonPath("$[1].name").value("TestStudent2"))
                .andExpect(jsonPath("$[1].age").value(26));
    }

}