/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.schedule;

/**
 *
 * @author suk22
 */
public class CourseScheduleModel {

    private int year;          // 학년도
    private int semester;      // 학기
    private String building;   // 건물명 (산학협력관 / 정보공학관)
    private String room;       // 강의실 번호
    private String day;        // 요일 (월, 화, ...)
    private int slot;          // 교시 (1~8)
    private String subject;    // 과목명
    private String professor;  // 교수명

    public CourseScheduleModel(int year, int semester,
                               String building, String room,
                               String day, int slot,
                               String subject, String professor) {
        this.year = year;
        this.semester = semester;
        this.building = building;
        this.room = room;
        this.day = day;
        this.slot = slot;
        this.subject = subject;
        this.professor = professor;
    }

    public int getYear() {
        return year;
    }

    public int getSemester() {
        return semester;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }

    public String getDay() {
        return day;
    }

    public int getSlot() {
        return slot;
    }

    public String getSubject() {
        return subject;
    }

    public String getProfessor() {
        return professor;
    }

    public static CourseScheduleModel fromLine(String line) {
        String[] arr = line.split(",", 8);
        if (arr.length < 8) {
            throw new IllegalArgumentException("잘못된 강의 시간 데이터: " + line);
        }

        int year = Integer.parseInt(arr[0]);
        int semester = Integer.parseInt(arr[1]);
        String building = arr[2];
        String room = arr[3];
        String day = arr[4];
        int slot = Integer.parseInt(arr[5]);
        String subject = arr[6];
        String professor = arr[7];

        return new CourseScheduleModel(year, semester, building, room, day, slot, subject, professor);
    }


    public String toLine() {
        return String.join(",",
                String.valueOf(year),
                String.valueOf(semester),
                building,
                room,
                day,
                String.valueOf(slot),
                subject.replace(",", " "),
                professor.replace(",", " ")
        );
    }
}
