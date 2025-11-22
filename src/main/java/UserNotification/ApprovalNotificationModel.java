package UserNotification;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApprovalNotificationModel {

    private static final String APPROVAL_NOTIFY_FILE = "src/main/resources/approval_notify.txt";
    private static final String CANCEL_NOTIFY_FILE = "src/main/resources/cancel_notify.txt";

    public List<String> pollNotifications(String studentId) {
        List<String> result = new ArrayList<>();

        // 1) 승인/거절 알림 읽기
        result.addAll(readApprovalNotifications(studentId));

        // 2) 관리자 취소 알림 읽기
        result.addAll(readCancelNotifications(studentId));

        return result;
    }

    // ------------------------------------------------------
    // 승인/거절 알림 처리
    // ------------------------------------------------------
    private List<String> readApprovalNotifications(String studentId) {
        List<String> result = new ArrayList<>();
        List<String> remain = new ArrayList<>();

        File f = new File(APPROVAL_NOTIFY_FILE);
        if (!f.exists()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }

                String sid = parts[0].trim();
                String status = parts[1].trim();
                String date = parts[2].trim();
                String room = parts[3].trim();

                if (sid.equals(studentId)) {
                    result.add(String.format(
                            "%s 날짜에 신청한 %s 예약이 '%s' 처리되었습니다.",
                            date, room, status
                    ));
                } else {
                    remain.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일 갱신 (남은 알림만 다시 저장)
        rewriteFile(APPROVAL_NOTIFY_FILE, remain);

        return result;
    }

    // ------------------------------------------------------
    // 관리자 취소 알림 처리
    // ------------------------------------------------------
    private List<String> readCancelNotifications(String studentId) {
        List<String> result = new ArrayList<>();
        List<String> remain = new ArrayList<>();

        File f = new File(CANCEL_NOTIFY_FILE);
        if (!f.exists()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue;
                }

                String sid = parts[0].trim();  // 학번
                String status = parts[1].trim();  // 관리자취소
                String reason = parts[2].trim();  // 사유

                if (sid.equals(studentId)) {
                    result.add(String.format(
                            "관리자에 의해 예약이 취소되었습니다.\n사유: %s",
                            reason
                    ));
                } else {
                    remain.add(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일 갱신
        rewriteFile(CANCEL_NOTIFY_FILE, remain);

        return result;
    }

    // ------------------------------------------------------
    // 공통: 파일 다시 쓰기
    // ------------------------------------------------------
    private void rewriteFile(String path, List<String> remain) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, false), StandardCharsets.UTF_8))) {

            for (String line : remain) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
