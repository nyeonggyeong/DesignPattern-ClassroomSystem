package dashboard;

import ruleagreement.RuleAgreementController;

import javax.swing.*;
import java.io.BufferedWriter;
import java.net.Socket;

public class UserDashboard implements Dashboard {
    private String subRole;  // student / professor
    private String userId;
    private Socket socket;
    private BufferedWriter out;

    public UserDashboard(String subRole, String userId, Socket socket, BufferedWriter out) {
        this.subRole = subRole;
        this.userId = userId;
        this.socket = socket;
        this.out = out;
    }

    @Override
    public void show() {
        System.out.println("[DEBUG] UserDashboard 호출, subRole: " + subRole);//패턴 적용 확인용
        try {
            // RuleAgreementController 호출
            RuleAgreementController rac = new RuleAgreementController(userId, subRole, socket, out);
            rac.showView();

            // 학생/교수 기능 분기 (필요 시)
            if ("student".equalsIgnoreCase(subRole)) {
                System.out.println("학생 기능 활성화: 강의실 예약, 신청 확인 등");
            } else if ("professor".equalsIgnoreCase(subRole)) {
                System.out.println("교수 기능 활성화: 승인, 강의실 관리 등");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "사용자 대시보드 표시 오류: " + e.getMessage());
        }
    }
}
