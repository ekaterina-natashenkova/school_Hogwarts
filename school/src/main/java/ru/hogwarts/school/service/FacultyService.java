package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for faculty student");
        logger.debug("Was create faculty with ID: {}", faculty.getId());
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyId(Long id) {
        logger.info("Was invoked method for get faculty by id");
        logger.warn("Faculty with ID {} not faund", id);
        return facultyRepository.findById(id).get();
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty");
        logger.warn("Attempt to update a non-existent faculty with ID: {}", faculty.getId());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty by id");
        logger.debug("Faculty with ID {} successfully removed", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> filterColor(String color) {
        logger.info("Was invoked method for filtering faculty by color");
        logger.debug("Filtering faculty by color: {}", color);
        return facultyRepository.findAll().stream()
                .filter(faculty -> Objects.equals(faculty.getColor(), color))
                .collect(Collectors.toList());
    }

    public Collection<Faculty> findByNameOrColor(String findParameter) {
        logger.info("Was invoked method for search faculty by name or color");
        logger.debug("Search faculty by name or color - findParameter: {}", findParameter);
        return facultyRepository.findByNameOrColorIgnoreCase(findParameter, findParameter);
    }

    public Collection<Student> getStudentsByFaculty(Long id) {
        logger.info("Was invoked method for get students faculty by id");
        logger.debug("Get students faculty by ID: {}", id);
        return facultyRepository.findById(id).map(Faculty::getStudents).orElseThrow();
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name with stream");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Get longest faculty name");
        return faculties.stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

}