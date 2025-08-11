package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private long countFaculty = 0;
    private final Map<Long, Faculty> faculties;
    private final FacultyRepository facultyRepository;

    public FacultyService(long countFaculty, Map<Long, Faculty> faculties, FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        this.countFaculty++;
        this.faculties = new HashMap<>();
    }

    public long getCountFaculty() {
        return countFaculty;
    }

    public Map<Long, Faculty> getFaculty() {
        return faculties;
    }

    public Faculty createFaculty(Faculty faculty) {
        countFaculty++;
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFacultyId(Long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) {
            return null;
        }
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }

    public Collection<Faculty> filterColor(String color) {
        return faculties.values().stream()
                .filter(faculty -> Objects.equals(faculty.getColor(), color))
                .collect(Collectors.toList());
    }

}