/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package management;

import management.schedule.CourseScheduleController;
import calendar.CalendarController;
import calendar.ReservationRepositoryModel;
import calendar.ReservationServiceModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import rulemanagement.RuleManagementController;
import visualization.MainView;
import visualization.ReservationController;
import visualization.ReservationModel;

/**
 *
 * @author suk22
 * @author 염승욱
 * @since 2025-11-10
 * @modified 백업 버튼 및 기능 추가
 */
public class ReservationMgmtView extends javax.swing.JFrame implements ReservationMgmtObserver {

    private final NotificationController notificationController = new NotificationController();
    private ReservationMgmtController controller;
    private ReservationMgmtDataModel dataModel;
    private boolean suppressTableListener = false;
    private boolean isApplyingApproval = false;
    private Timer autoRefreshTimer;
    private BufferedReader in;
    private BufferedWriter out;

    // 메인 생성자
    public ReservationMgmtView(BufferedReader in, BufferedWriter out, String userId) {
        this.in = in;
        this.out = out;

        // ConcreteSubject 생성
        this.dataModel = new ReservationMgmtDataModel();

        // Controller 에 모델 주입
        if (in != null && out != null) {
            this.controller = new ReservationMgmtController(in, out, dataModel);
        } else {
            this.controller = new ReservationMgmtController(dataModel);
        }

        // 자신을 Observer 로 등록
        this.dataModel.registerObserver(this);

        initComponents();
        setupTableListener();
        setupApprovalColumnEditor();

        controller.loadReservationsFromFile();
        startAutoRefresh();
        commonInit();
    }

    // 보조 생성자들
    public ReservationMgmtView() {
        this(null, null, null);
    }

    public ReservationMgmtView(BufferedReader in, BufferedWriter out) {
        this(in, out, null);
    }

    public ReservationMgmtView(String userId) {
        this(null, null, userId);
    }

    // 공통 초기화
    private void commonInit() {
        setTitle("관리자 예약 목록");
        setLocationRelativeTo(null);
        notificationController.startMonitoring();
        setVisible(true);
    }

    // Observer 콜백 : 모델이 바뀔 때마다 호출됨
    @Override
    public void update() {
        List<ReservationMgmtModel> list = dataModel.getReservations();
        refreshTable(list);
    }

    // JTable 갱신
    private void refreshTable(List<ReservationMgmtModel> reservations) {
        suppressTableListener = true;
        try {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            for (ReservationMgmtModel r : reservations) {
                model.addRow(new Object[]{
                    r.getName(),
                    r.getDepartment(),
                    r.getStudentId(),
                    r.getRoom(),
                    r.getDate(),
                    r.getTime(),
                    r.getApproved()
                });
            }
        } finally {
            suppressTableListener = false;
        }
    }

    // 승인 여부 콤보박스 에디터 설정
    private void setupApprovalColumnEditor() {
        String[] statusOptions = {"예약대기", "승인", "거절"};
        JComboBox<String> comboBox = new JComboBox<>(statusOptions);
        TableColumn statusColumn = jTable1.getColumnModel().getColumn(6);
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    // 테이블 변경 리스너 (승인 상태 변경 시 컨트롤러 호출)
    private void setupTableListener() {
        jTable1.getModel().addTableModelListener(e -> {

            if (suppressTableListener) {
                return;
            }
            if (isApplyingApproval) {
                return;
            }

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0 || col != 6) {
                return; // 승인 여부 컬럼만 처리
            }
            String studentId = String.valueOf(jTable1.getValueAt(row, 2));
            String newStatus = String.valueOf(jTable1.getValueAt(row, 6));

            try {
                isApplyingApproval = true;

                controller.updateApprovalStatus(studentId, newStatus);

                if ("승인".equals(newStatus)) {
                    JOptionPane.showMessageDialog(this,
                            "예약이 승인되었습니다.", "처리 완료", JOptionPane.INFORMATION_MESSAGE);
                } else if ("거절".equals(newStatus)) {
                    JOptionPane.showMessageDialog(this,
                            "예약이 거절되었습니다.", "처리 완료", JOptionPane.INFORMATION_MESSAGE);
                } else if ("관리자취소".equals(newStatus)) {
                    JOptionPane.showMessageDialog(this,
                            "예약이 관리자 권한으로 취소되었습니다.", "처리 완료", JOptionPane.INFORMATION_MESSAGE);
                }

            } finally {
                isApplyingApproval = false;
            }
        });
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(3000, e -> controller.loadReservationsFromFile());
        autoRefreshTimer.start();
    }

    @Override
    public void dispose() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "이름", "학과", "학번", "강의실", "날짜", "시간", "승인 여부"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("사용규칙 관리");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("날짜 제한");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("시작화 자료");
        jButton3.setMaximumSize(new java.awt.Dimension(82, 23));
        jButton3.setMinimumSize(new java.awt.Dimension(82, 23));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("강의실 정보");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("맑은 고딕", 0, 26)); // NOI18N
        jLabel2.setText("예약 리스트");

        jButton5.setText("로그아웃");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("제한");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("해제");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("예약/취소 통계");
        jButton8.setMaximumSize(new java.awt.Dimension(82, 23));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jTextField1.setText("검색해 주세요");

        jButton9.setText("검색");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("알림");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("공지사항 관리");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("백업");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("관리자 취소");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("취소된 예약 목록");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("학년도/학기별");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(232, 232, 232)
                        .addComponent(jLabel2)
                        .addContainerGap(333, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addGap(6, 6, 6))))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton14)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jButton12))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton4)
                    .addComponent(jButton11)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15))
                .addGap(15, 15, 15))
        );

        jButton8.getAccessibleContext().setAccessibleName("예약취소 통계");
        jButton15.getAccessibleContext().setAccessibleName("학년도/학기별");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            RuleManagementController controller = new RuleManagementController("src/main/resources/rules.txt");
            controller.showView();
            this.dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "규칙 화면 열기 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ReservationRepositoryModel repo = new ReservationRepositoryModel();
        ReservationServiceModel service = new ReservationServiceModel(repo);
        new CalendarController(service);  // 생성자에서 CalendarView 띄움
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ReservationModel model = new ReservationModel();
        ReservationController controller = new ReservationController(model);
        new MainView(model, controller);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            login.LoginView view = new login.LoginView();
            login.LoginModel model = new login.LoginModel();
            new login.LoginController(view, model);
            view.setVisible(true);

            this.dispose();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "제한할 사용자를 선택하세요.");
            return;
        }
        String studentId = (String) jTable1.getValueAt(selectedRow, 2);
        controller.banUser(studentId);
        JOptionPane.showMessageDialog(this, studentId + " 사용자가 제한되었습니다.");

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String studentId = JOptionPane.showInputDialog(this, "해제할 사용자의 학번을 입력하세요.");
        if (studentId != null && !studentId.isEmpty()) {
            controller.unbanUser(studentId);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        String keyword = jTextField1.getText().trim();
        if (keyword.isEmpty()) {
            List<ReservationMgmtModel> all = dataModel.getReservations();
            refreshTable(all);
            return;
        }

        List<ReservationMgmtModel> reservations = dataModel.getReservations();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // 기존 데이터 초기화

        for (ReservationMgmtModel r : reservations) {
            if (r.getName().contains(keyword)
                    || r.getDepartment().contains(keyword)
                    || r.getStudentId().contains(keyword)
                    || r.getRoom().contains(keyword)) {
                model.addRow(new Object[]{
                    r.getName(), r.getDepartment(), r.getStudentId(),
                    r.getRoom(), r.getDate(), r.getTime(), r.getApproved()
                });
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ClassroomView classroomView = new ClassroomView();
        classroomView.setVisible(true);
        classroomView.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        UserStatsController userStatsController = new UserStatsController();
        userStatsController.showUserStatsUI();
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        notificationController.showNotificationView();
        notificationController.refreshNotifications();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                notice.NoticeRepository repo = new notice.NoticeRepository("src/main/resources/Notice.txt");
                notice.NoticeModel model = new notice.NoticeModel(repo);
                notice.NoticeEditorView editorView = new notice.NoticeEditorView(this); // this = 현재 JFrame
                notice.NoticeListView listView = new notice.NoticeListView();
                new notice.NoticeController(model, listView, editorView);
                listView.setLocationRelativeTo(null);
                listView.setVisible(true);
                this.dispose(); // 현재 화면 닫기 (필요에 따라)
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }//GEN-LAST:event_jButton11ActionPerformed

    // 백업 버튼 기능
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        if (this.out == null || this.in == null) {
            JOptionPane.showMessageDialog(this, "서버와의 연결을 확인하세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "서버의 모든 파일들을 백업하시겠습니까?\n(오늘 날짜로 새 폴더가 생성됩니다)",
                "서버 백업 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.write("BACKUP_REQUEST");
                out.newLine();
                out.flush();
                // 서버의 응답 대기(잠시 UI 스레드를 멈춤)
                String response = in.readLine();

                if (response != null && response.equals("BACKUP_SUCCESS")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "서버 백업이 완료되었습니다",
                            "백업 성공",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "서버 백업실패: " + response,
                            "백업 오류",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "서버 통신 오류 발생" + e.getMessage(),
                        "통신오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택하세요.");
            return;
        }

        // 관리자 취소 확인 팝업
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "정말로 해당 예약을 관리자 권한으로 취소하시겠습니까?",
                "관리자 예약 취소 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String studentId = jTable1.getValueAt(selectedRow, 2).toString();

        // 취소 사유 입력
        String reason = JOptionPane.showInputDialog(
                this,
                "취소 사유를 입력하세요:",
                "취소 사유 입력",
                JOptionPane.PLAIN_MESSAGE
        );

        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "취소 사유를 반드시 입력해야 합니다.");
            return;
        }

        boolean success = controller.cancelReservationByAdmin(studentId, reason);

        // 성공 여부에 따라 메시지
        if (success) {
            notificationController.notifyAdminCancel();
            JOptionPane.showMessageDialog(this, "관리자 취소가 완료되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "관리자 취소 처리 중 오류가 발생했습니다.");
        }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        CanceledReservationView view = new CanceledReservationView();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        CourseScheduleController.open();
    }//GEN-LAST:event_jButton15ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
