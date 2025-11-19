/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

//터미널 구현 파일(GUIController.java 파일로 볼 것)
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 *
 * @author scq37
 *
 */
public class ReservationController {  //예약 제어 클래스

    private static final String EXCEL_PATH = "src/main/resources/available_rooms.xlsx";
    private static final List<String> LAB_ROOMS = Arrays.asList("911", "915", "916");

    private List<RoomModel> allRooms = new ArrayList<>();
    private ConsoleView view = new ConsoleView(); //사용자 인터페이스 객체 생성

    

    private boolean isLabRoom(String roomNumber) {
        return roomNumber.equals("911")
                || roomNumber.equals("915")
                || roomNumber.equals("916")
                || roomNumber.equals("918"); // 새로 추가
    }



    public boolean isUserBanned(String studentId) {
        List<String> bannedUsers = getBannedUsers();
        return bannedUsers.contains(studentId);
    }
    
 private List<String> getBannedUsers() {
        List<String> bannedUsers = new ArrayList<>();
        String filePath = "src/main/resources/banlist.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                bannedUsers.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("제한 사용자 파일 읽기 실패: " + e.getMessage());
        }

        return bannedUsers;
    }
}
