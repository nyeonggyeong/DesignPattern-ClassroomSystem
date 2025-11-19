/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UserFunction;

/**
 *
 * @author jms5310
 */
import UserNotification.NotificationButton;
import UserNotification.ApprovalNotificationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserMainView extends JFrame {

    private JLabel welcomeLabel;
    private JLabel dateLabel;
    private JButton viewReservationsButton;
    private JButton createReservationButton;
    private JButton noticeButton;
    private JButton logoutButton;
    private NotificationButton notificationButton;
    private final String studentId;
    private ApprovalNotificationController approvalNotificationController;

    // 🔥 수정: studentId를 받는 생성자만 사용해야 한다
    public UserMainView(String studentId) {
        this.studentId = studentId;
        // 기본 프레임 설정
        setTitle("강의실 예약 시스템 - 사용자 메뉴");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI 초기화
        initUI();

        approvalNotificationController = new ApprovalNotificationController(studentId, this);
        approvalNotificationController.showPendingNotificationsOnLogin(); // 로그인 시 바로 알림
        approvalNotificationController.startPolling(); // 푸시처럼 주기 확인

        setVisible(true);
    }

    private void initUI() {
        // 상단 패널 - 환영 메시지
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 중앙 패널 - 기능 버튼들
        JPanel centerPanel = createButtonPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널 - 로그아웃 버튼
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 메인 환영 메시지
        welcomeLabel = new JLabel("환영합니다", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        // 현재 시간 정보 
        dateLabel = new JLabel(new java.util.Date().toString(), SwingConstants.RIGHT);
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        panel.add(dateLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 예약 조회 버튼
        viewReservationsButton = createFunctionButton("내 예약 조회/취소", "예약 목록을 확인하고 취소할 수 있습니다.");
        panel.add(viewReservationsButton);

        // 강의실 예약 버튼
        createReservationButton = createFunctionButton("강의실 예약하기", "새로운 강의실을 예약합니다.");
        panel.add(createReservationButton);

        // 공지사항 버튼
        noticeButton = createFunctionButton("공지사항 확인하기", "관리자가 등록한 공지사항을 확인합니다.");
        panel.add(noticeButton);

        return panel;
    }

    private JButton createFunctionButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setPreferredSize(new Dimension(200, 60));
        return button;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // notificationButton은 나중에 setNotificationButton()으로 설정
        leftPanel.add(new JLabel("알림: ")); // 플레이스홀더

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButton = new JButton("로그아웃");
        logoutButton.setFocusPainted(false);
        rightPanel.add(logoutButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // 알림 버튼 표시용
    public void setNotificationButton(NotificationButton button) {
        this.notificationButton = button;

        JPanel footerPanel = (JPanel) getContentPane().getComponent(2); // 하단 패널
        JPanel leftPanel = (JPanel) ((JPanel) footerPanel).getComponent(0); // 왼쪽

        leftPanel.removeAll();
        leftPanel.add(new JLabel("알림: "));
        leftPanel.add(notificationButton);

        leftPanel.revalidate();
        leftPanel.repaint();
    }

    // 사용자 환영 메시지
    public void setWelcomeMessage(String userId) {
        welcomeLabel.setText("환영합니다, " + userId + "님");
    }

    public void addViewReservationsListener(ActionListener listener) {
        viewReservationsButton.addActionListener(listener);
    }

    public void addCreateReservationListener(ActionListener listener) {
        createReservationButton.addActionListener(listener);
    }

    public void addNoticeListener(ActionListener listener) {
        noticeButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public NotificationButton getNotificationButton() {
        return notificationButton;
    }

    // 종료 시 알림 타이머 해제
    @Override
    public void dispose() {
        if (approvalNotificationController != null) {
            approvalNotificationController.stop();
        }
        super.dispose();
    }
}
