package UserNotification;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ApprovalNotificationController {

    private final ApprovalNotificationModel model;
    private final String studentId;
    private final JFrame parent;
    private Timer timer;

    public ApprovalNotificationController(String studentId, JFrame parent) {
        this.model = new ApprovalNotificationModel();
        this.studentId = studentId;
        this.parent = parent;
    }

    public void showPendingNotificationsOnLogin() {
        List<String> messages = model.pollNotifications(studentId);
        if (messages.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append("• ").append(msg).append("\n");
        }

        JOptionPane.showMessageDialog(
                parent,
                sb.toString(),
                "예약 승인/거절 알림",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void startPolling() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> messages = model.pollNotifications(studentId);
                if (messages.isEmpty()) return;

                SwingUtilities.invokeLater(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (String msg : messages) {
                        sb.append("• ").append(msg).append("\n");
                    }

                    JOptionPane.showMessageDialog(
                            parent,
                            sb.toString(),
                            "예약 승인/거절 알림",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });
            }
        }, 0, 5_000); // 5초마다 체크
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
