/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.schedule;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author suk22
 */
public class CourseScheduleService {

    private final CourseScheduleRepository repository;

    public CourseScheduleService(CourseScheduleRepository repository) {
        this.repository = repository;
    }

    public List<CourseScheduleModel> getSchedule(int year, int semester,
                                                 String building, String room) {
        return repository.loadAll().stream()
                .filter(m -> m.getYear() == year
                        && m.getSemester() == semester
                        && m.getBuilding().equals(building)
                        && m.getRoom().equals(room))
                .collect(Collectors.toList());
    }


    public boolean isConflict(int year, int semester,
                              String building, String room,
                              String day, int slot) {
        return repository.loadAll().stream()
                .anyMatch(m ->
                        m.getYear() == year
                        && m.getSemester() == semester
                        && m.getBuilding().equals(building)
                        && m.getRoom().equals(room)
                        && m.getDay().equals(day)
                        && m.getSlot() == slot
                );
    }

    public boolean addSchedule(CourseScheduleModel model) {
        if (isConflict(model.getYear(), model.getSemester(),
                model.getBuilding(), model.getRoom(),
                model.getDay(), model.getSlot())) {
            return false;
        }
        repository.save(model);
        return true;
    }

    public void removeSchedule(CourseScheduleModel model) {
        repository.delete(model);
    }
}
