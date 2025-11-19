package UserNotification;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApprovalNotificationModel {

    private static final String APPROVAL_NOTIFY_FILE = "src/main/resources/approval_notify.txt";

    public List<String> pollNotifications(String studentId) {
        List<String> result = new ArrayList<>();
        List<String> remain = new ArrayList<>();

        File f = new File(APPROVAL_NOTIFY_FILE);
        if (!f.exists()) return result;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String sid = parts[0].trim();
                String status = parts[1].trim();
                String date = parts[2].trim();
                String room = parts[3].trim();

                if (sid.equals(studentId)) {
                    result.add(String.format("%s 날짜에 신청한 %s 예약이 '%s' 처리되었습니다.", date, room, status));
                } else {
                    remain.add(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일 갱신
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            for (String line : remain) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
