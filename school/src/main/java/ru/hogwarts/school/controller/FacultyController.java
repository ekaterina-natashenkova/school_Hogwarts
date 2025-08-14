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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;

/**
 * ResponseEntity.notFound().build() - когда ресурс не был найден, поскольку это соответствует коду состояния HTTP 404 Not Found.
 * ResponseEntity.status(HttpStatus.BAD_REQUEST).build() - когда запрос от клиента был неверным или не соответствует требованиям сервера.
 */

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        Faculty faculty1 = facultyService.createFaculty(faculty);
        return ResponseEntity.ok(faculty1);
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyId(@PathVariable Long id) {
        Faculty faculty = facultyService.getFacultyId(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping()
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        Faculty faculty2 = facultyService.updateFaculty(faculty);
        if (faculty2 == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty2);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filterColor")
    public ResponseEntity<Collection<Faculty>> filterColorFaculty (@RequestParam String color) {
        if (color != null && color.isBlank()){
            return ResponseEntity.ok(facultyService.filterColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

}