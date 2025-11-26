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
import management.iterator.*;
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
    private ReservationMgmtDataModel dataModel;
    private BufferedWriter out;
    private BufferedReader in;

    private final java.util.Map<String, ReservationMgmtModel> modelCache = new java.util.HashMap<>();

    public ReservationMgmtController() {
    }

    public ReservationMgmtController(ReservationMgmtDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public ReservationMgmtController(BufferedReader in, BufferedWriter out, ReservationMgmtDataModel dataModel) {
        this.in = in;
        this.out = out;
        this.dataModel = dataModel;
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

    public void loadReservationsFromFile() { // 메서드 이름 변경 권장 (getAllReservations -> loadReservationsFromFile)
        // * 기존 getAllReservations 로직과 동일하지만 리턴하지 않고 dataModel에 set 함
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
                String approved = data[12];   // 예약 상태

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
                    // 캐시된 모델 상태 동기화
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

        // [중요] 데이터를 모델에 넣어줌 -> 여기서 notifyObservers()가 발생하여 View가 갱신됨
        if (dataModel != null) {
            dataModel.setReservations(reservations);
        }
    }

    public void updateApprovalStatus(String studentId, String newStatus) {
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

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
            if (dataModel != null) {
                dataModel.updateApprovalInMemory(studentId, newStatus);
            }

            ReservationMgmtModel model = modelCache.get(studentId);
            if (model != null) {
                if (newStatus.equals("승인") || newStatus.equals("거절")) {
                    appendApprovalNotification(model);
                }
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

            loadReservationsFromFile();
        }
    }

    public void unbanUser(String studentId) {
        List<String> bannedUsers = getBannedUsers();
        if (!bannedUsers.contains(studentId)) {
            return;
        }
        bannedUsers.remove(studentId);
        saveBannedUsers(bannedUsers);

        loadReservationsFromFile();
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
        if (dataModel != null) {
            return dataModel.searchReservations(name, studentId, room);
        }
        return new ArrayList<>();
    }

    public boolean cancelReservationByAdmin(String studentId, String reason) {
        ReservationCollection collection = new ReservationCollection(FILE_PATH);
        Iterator<Reservation> it = collection.createIterator();

        List<String> updated = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        boolean isCancelled = false;

        while (it.hasNext()) {
            Reservation r = it.next();
            if (r.getRawLine() == null || r.getRawLine().trim().isEmpty()) {
                continue;
            }

            try {
                if (r.getStudentId().equals(studentId)) {
                    removed.add(r.getRawLine());
                    isCancelled = true;
                } else {
                    updated.add(r.getRawLine());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                updated.add(r.getRawLine());
                System.out.println("잘못된 데이터 형식 발견: " + r.getRawLine());
            }
        }

        if (!isCancelled) {
            System.out.println("취소 대상 학생을 찾지 못했습니다: " + studentId);
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH, false), StandardCharsets.UTF_8))) {
            for (String line : updated) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/resources/cancelled_reservations.txt", true), StandardCharsets.UTF_8))) {
            for (String line : removed) {
                writer.write(line + ",관리자 취소," + reason);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/resources/cancel_notify.txt", true), StandardCharsets.UTF_8))) {
            writer.write(studentId + ",관리자 취소," + reason);
            writer.newLine();
        } catch (Exception ignored) {
        }

        loadReservationsFromFile();

        return true;
    }
}
