package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentCaptor.forClass;

@WebMvcTest(FacultyController.class)
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
        Faculty testFaculty = getTestFaculty("TestFaculty", "RandomColor");

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", testFaculty.getName());
        facultyObject.put("color", testFaculty.getColor());

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Просмотр добавленного факультета по id")
    void getFacultyId() throws Exception {
        Faculty testFaculty = getTestFaculty(10L, "TestFaculty", "RandomColor");

        when(facultyService.getFacultyId(anyLong())).thenReturn(testFaculty);

        mockMvc.perform(get("/faculty/" + testFaculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFaculty.getId()))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Обновление данных факультета")
    void updateFaculty() throws Exception {
        Faculty testFaculty = getTestFaculty(10L, "TestFaculty", "RandomColor");

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", testFaculty.getName());
        facultyObject.put("color", testFaculty.getColor());

        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty", testFaculty.getId())
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFaculty.getId()))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Удаление добавленного факультета по id")
    void deleteFaculty() throws Exception {
        Faculty testFaculty = getTestFaculty(10L, "TestFaculty", "RandomColor");

        doNothing().when(facultyService).deleteFaculty(testFaculty.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", testFaculty.getId()))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(testFaculty.getId());
    }

    @Test
    @DisplayName("Фильтрация факультетов по цвету")
    void filterColorFaculty() throws Exception {
        Faculty testFaculty = getTestFaculty(10L, "TestFaculty", "TestColor");
        String colorFilter = "TestColor";

        Collection<Faculty> facultyByColor = Collections.singletonList(testFaculty);

        when(facultyService.filterColor(colorFilter)).thenReturn(facultyByColor);

        mockMvc.perform(get("/faculty/filterColor")
                        .param("color", colorFilter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFaculty.getId()))
                .andExpect(jsonPath("$[0].name").value(testFaculty.getName()))
                .andExpect(jsonPath("$[0].color").value(testFaculty.getColor()));

        verify(facultyService, times(1)).filterColor(colorFilter);
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

        when(facultyService.findByNameOrColor(anyString())).thenAnswer(invocation -> {
            String param = invocation.getArgument(0, String.class);
            if (param.equals(findName)) {
                return findByName;
            } else if (param.equals(findColor)) {
                return findByColor;
            }
            return Collections.emptyList();
        });

        mockMvc.perform(get("/faculty/nameOrColor")
                        .param("findParameter", findName) // Измените имя параметра
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testFaculty1.getId()))
                .andExpect(jsonPath("$[0].name").value("TestFaculty1"))
                .andExpect(jsonPath("$[0].color").value("RandomColor1"));

        mockMvc.perform(get("/faculty/nameOrColor")
                        .param("findParameter", findColor) // Измените имя параметра
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testFaculty2.getId()))
                .andExpect(jsonPath("$[0].name").value("TestFaculty2"))
                .andExpect(jsonPath("$[0].color").value("RandomColor2"));

        verify(facultyService, times(2)).findByNameOrColor(anyString()); // Метод вызывается 2 раза

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(facultyService, times(2)).findByNameOrColor(captor.capture());
        List<String> allValues = captor.getAllValues();
        assertEquals(findName, allValues.get(0));
        assertEquals(findColor, allValues.get(1));
    }

    @Test
    @DisplayName("Получение списка студентов заданного факультета")
    void getStudentsByFaculty() throws Exception {
        Student testStudent1 = getTestStudent(11L, "TestStudent1", 24);
        Student testStudent2 = getTestStudent(12L, "TestStudent2", 26);
        long facultyId = 1L;

        Collection<Student> studentsByFaculty = Arrays.asList(testStudent1, testStudent2);

        when(facultyService.getStudentsByFaculty(facultyId)).thenReturn(studentsByFaculty);

        mockMvc.perform(get("/faculty/getStudentsFaculty/{id}", facultyId)
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