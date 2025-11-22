/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import management.state.ReservationContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.List;
import javax.swing.JOptionPane;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author suk22
 */
public class ReservationMgmtController {

    private static final String FILE_PATH = "src/main/resources/reservation.txt";
    private static final String BAN_LIST_FILE = "src/main/resources/banlist.txt";
    private static final String ALL_FILES_DIR = "src/main/resources/";
    private static final String APPROVAL_NOTIFY_FILE = "src/main/resources/approval_notify.txt";
    private BufferedWriter out;
    private BufferedReader in;

    public static final String EVT_RESERVATIONS = "reservations";

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final java.util.Map<String, ReservationMgmtModel> modelCache = new java.util.HashMap<>();

    public ReservationMgmtController() {
    }

    public ReservationMgmtController(BufferedReader in, BufferedWriter out) {
        this.in = in;
        this.out = out;
    }

    public void saveAllFiles() {
        String fileName = "";
        File files = new File(ALL_FILES_DIR);

        File[] fileList = files.listFiles();

        for (File file : fileList) {
            if (file.isFile()) {
                fileName = file.getName();
                System.out.println("fileName: " + fileName);
            }
        }
    }

    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public List<ReservationMgmtModel> getAllReservations() {
        List<ReservationMgmtModel> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");
                if (data.length < 13) {
                    continue;
                }

                String name = data[0];
                String userType = data[1];
                String studentId = data[2];
                String department = data[3];
                String building = data[4];
                String roomType = data[5];
                String roomNumber = data[6];
                String date = data[7];
                String day = data[8];
                String startTime = data[9];
                String endTime = data[10];
                String purpose = data[11];
                String approved = data[12];   // 예약 상태 → State 패턴에서 context로 전환됨

                String displayName = name + " (" + userType + ")";
                String roomDisplay = building + " / " + roomNumber;
                String timeDisplay = startTime + " ~ " + endTime;

                ReservationMgmtModel model = modelCache.get(studentId);

                if (model == null) {
                    // 최초 생성
                    model = new ReservationMgmtModel(
                            displayName, studentId, department,
                            roomDisplay, date, timeDisplay, approved
                    );
                    modelCache.put(studentId, model);

                } else {
                    if (!model.getApproved().equals(approved)) {
                        if (approved.equals("승인")) {
                            model.approve();
                        } else if (approved.equals("거절")) {
                            model.reject();
                        } else if (approved.equals("관리자취소")) {
                            model.cancelByAdmin();  
                        } else {
                            model.setPending();
                        }
                    }
                }

                reservations.add(model);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public void updateApprovalStatus(String studentId, String newStatus) {
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        // [KEEP] 파일 읽기/라인 업데이트
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 13 && data[2].equals(studentId)) {
                    data[12] = newStatus;
                    found = true;
                    updatedLines.add(String.join(",", data));
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // [KEEP] 파일 쓰기
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH, false), StandardCharsets.UTF_8))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            ReservationMgmtModel model = modelCache.get(studentId);

            if (model != null) {

                if (newStatus.equals("승인")) {
                    model.approve();
                    appendApprovalNotification(model);

                } else if (newStatus.equals("거절")) {
                    model.reject();
                    appendApprovalNotification(model);

                } else if (newStatus.equals("관리자취소")) {
                    model.cancelByAdmin();   // ✅ 이것만 추가
                } else {
                    model.setPending();
                }
            } else {
                getAllReservations();
            }
        }
    }

    private void appendApprovalNotification(ReservationMgmtModel m) {
        // 포맷: 학번,승인여부,날짜,강의실
        String line = String.join(",",
                m.getStudentId(),
                m.getApproved(),
                m.getDate(),
                m.getRoom()
        );

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(APPROVAL_NOTIFY_FILE, true), StandardCharsets.UTF_8))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getBannedUsers() {
        List<String> bannedUsers = new ArrayList<>();
        File f = new File(BAN_LIST_FILE);
        if (!f.exists()) {
            return bannedUsers;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bannedUsers.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bannedUsers;
    }

    public void banUser(String studentId) {
        List<String> bannedUsers = getBannedUsers();
        if (!bannedUsers.contains(studentId)) {
            bannedUsers.add(studentId);
            saveBannedUsers(bannedUsers);
            pcs.firePropertyChange(EVT_RESERVATIONS, null, null);
        }
    }

    public void unbanUser(String studentId) {
        List<String> bannedUsers = getBannedUsers();
        if (!bannedUsers.contains(studentId)) {
            // 팝업은 View에서 띄우도록 유지
            return;
        }
        bannedUsers.remove(studentId);
        saveBannedUsers(bannedUsers);
        pcs.firePropertyChange(EVT_RESERVATIONS, null, null);
    }

    private void saveBannedUsers(List<String> bannedUsers) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(BAN_LIST_FILE, false), StandardCharsets.UTF_8))) {
            for (String id : bannedUsers) {
                writer.write(id);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserBanned(String studentId) {
        List<String> bannedUsers = getBannedUsers();
        return bannedUsers.contains(studentId);
    }

    public List<ReservationMgmtModel> searchReservations(String name, String studentId, String room) {
        List<ReservationMgmtModel> allReservations = getAllReservations();
        List<ReservationMgmtModel> filtered = new ArrayList<>();

        for (ReservationMgmtModel r : allReservations) {
            boolean match = true;

            if (name != null && !name.isEmpty() && !r.getName().contains(name)) {
                match = false;
            }
            if (studentId != null && !studentId.isEmpty() && !r.getStudentId().contains(studentId)) {
                match = false;
            }
            if (room != null && !room.isEmpty() && !r.getRoom().contains(room)) {
                match = false;
            }

            if (match) {
                filtered.add(r);
            }
        }

        return filtered;
    }

    public void cancelReservationByAdmin(String studentId, String reason) {

        updateApprovalStatus(studentId, "관리자취소");

        String line = String.join(",", studentId, "관리자취소", reason);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/resources/cancel_notify.txt", true), StandardCharsets.UTF_8))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
