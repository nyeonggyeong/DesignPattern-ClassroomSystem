/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author user
 */
public class ReservationWithStudentController {
    
    private List<String[]> studentList; // 학생 리스트 저장
    private List<String[]> selectedStudents; // 선택된 학생 리스트
    private BufferedReader in;
    private BufferedWriter out;
    private ReservationWithStudentView view;
    private ReservationGUIController parentController;
    private ReservationRepository repository;
    
    private RoomModel selectedRoom;
    private String reserveDate;
    private List<String> reserveTimes;
    private String reservePurpose;
    private String professorId;
    
    public ReservationWithStudentController(BufferedReader in, BufferedWriter out, ReservationGUIController parentController,
                                            RoomModel selectedRoom, String reserveDate, List<String> reserveTimes, String reservePurpose,
                                            String professorId) {
        this.in = in;
        this.out = out;
        this.parentController = parentController;
        this.selectedRoom = selectedRoom;
        this.reserveDate = reserveDate;
        this.reserveTimes = reserveTimes;
        this.reservePurpose = reservePurpose;
        this.professorId = professorId;
        this.studentList = new ArrayList<>();
        this.selectedStudents = new ArrayList<>();
        this.repository = new ReservationRepository();
        initializeUserInfoFromServer();
    }
    
    private void initializeUserInfoFromServer() {
        try {
            out.write("USER_INFO");
            out.newLine();
            out.flush();
            
            String message = in.readLine();
                if (message != null && message.startsWith("USER_INFO:")) {
                    String data = message.substring("USER_INFO:".length());
                    if (!data.isEmpty()) {
                        String[] students = data.split(";");
                        for (String student : students) {
                            String[] parts = student.split(",");
                            if (parts.length >= 2) {
                                studentList.add(new String[]{parts[0], parts[1], parts[2]});
                            }
                        }
                    }
                }
        } catch (IOException e) {
            System.out.println("에러발생: " + e.getMessage());
        }
    }
    
    public void setView(ReservationWithStudentView view) {
        this.view = view;
        if (!studentList.isEmpty()) {
            view.updateStudentList(studentList);
        }
    }
    
    public List<String[]> getStudentList() {
        return studentList;
    }
    
    public List<String[]> getSelectedStudents() {
        return selectedStudents;
    }
    
    public void addSelectedStudent(String studentId, String studentName, String dept) {
        boolean alreadySelected = selectedStudents.stream()
                .anyMatch(s -> s[0].equals(studentId));
        
        if (!alreadySelected) {
            selectedStudents.add(new String[]{studentId, studentName, dept});
            System.out.println("사용자 추가" + studentId + studentName);
        }
    }
    
    public void removeSelectedStudent(String studentId) {
        selectedStudents.removeIf(s -> s[0].equals(studentId));
        System.out.println("사용자 제거" + studentId);
    }
    
    public void confirmReservation() {
        if (selectedStudents.isEmpty()) {
            view.showMessage("최소 1명 이상의 학생을 선택해야합니다.");
            return;
        }
        
        parentController.setStatus("예약확정");
        
        parentController.saveNewReservation(); // 교수 예약
        
        saveStudentReservation(); // 학생 예약
        cancel();
    }
    
    public void saveStudentReservation() {
        String dayOfWeek = parentController.getDayOfWeek(reserveDate);
        List<Reservation> studentReservations = new ArrayList<>();
        
        for (String[] s : selectedStudents) {
            String id = s[0];
            String name = s[1];
            String dept = s[2];
            
            for (String time : reserveTimes) {
                String[] parts = time.split("~");
                if (parts.length == 2) {
                    String start = parts[0].trim();
                    String end = parts[1].trim();
                    
                    try {
                        // ✅ Builder 패턴 사용
                        Reservation reservation = new Reservation.Builder(
                                name,
                                "학생",
                                id,
                                dept,
                                selectedRoom.getBuilding(),
                                selectedRoom.getRoomNumber(),
                                reserveDate,
                                start,
                                end
                            )
                            .roomType(selectedRoom.getType())
                            .dayOfWeek(dayOfWeek)
                            .purpose(reservePurpose)
                            .status("예약확정")
                            .build();
                        
                        studentReservations.add(reservation);
                        
                    } catch (IllegalArgumentException e) {
                        view.showMessage("학생 예약 생성 실패: " + e.getMessage());
                        return;
                    }
                }
            }
        }
        
        // 일괄 저장
        boolean result = repository.saveAll(studentReservations);
        
        if (result) {
            view.showMessage(selectedStudents.size() + "명의 학생 예약이 완료되었습니다.");
        } else {
            view.showMessage("학생 예약 저장에 실패했습니다.");
        }        
    }
    
    public void cancel() {
        view.dispose();
        parentController.getView().setVisible(true);
    }
}
