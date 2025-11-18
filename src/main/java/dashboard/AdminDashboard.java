package dashboard;

import management.ReservationMgmtView;

import javax.swing.*;

public class AdminDashboard implements Dashboard {
    @Override
    public void show() {
        System.out.println("[DEBUG] AdminDashboard 호출");//패턴 확인용
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            new ReservationMgmtView().setVisible(true);
            System.out.println("관리자 기능 활성화");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "관리자 대시보드 표시 오류: " + e.getMessage());
        }
    }
}
