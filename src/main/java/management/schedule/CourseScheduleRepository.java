/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.schedule;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suk22
 */
public class CourseScheduleRepository {

    private static final Path FILE_PATH
            = Paths.get("src", "main", "resources", "course_schedule.txt");

    private void ensureFile() throws IOException {
        if (Files.notExists(FILE_PATH.getParent())) {
            Files.createDirectories(FILE_PATH.getParent());
        }
        if (Files.notExists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }
    }

    public List<CourseScheduleModel> loadAll() {
        List<CourseScheduleModel> result = new ArrayList<>();
        try {
            ensureFile();
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    result.add(CourseScheduleModel.fromLine(line));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void save(CourseScheduleModel model) {
        try {
            ensureFile();
            try (BufferedWriter writer = Files.newBufferedWriter(
                    FILE_PATH, StandardOpenOption.APPEND)) {
                writer.write(model.toLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(CourseScheduleModel target) {
        try {
            ensureFile();
            List<String> lines = Files.readAllLines(FILE_PATH);
            String targetLine = target.toLine();
            List<String> keep = new ArrayList<>();
            for (String line : lines) {
                if (!line.equals(targetLine)) {
                    keep.add(line);
                }
            }
            Files.write(FILE_PATH, keep,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
