package dashboard;

import java.io.BufferedWriter;
import java.net.Socket;

public class DashboardFactory {
    public static Dashboard createDashboard(String mainRole, String subRole, String userId, Socket socket, BufferedWriter out) {
        System.out.println("[DEBUG] DashboardFactory 호출 - mainRole: " + mainRole + ", subRole: " + subRole);//패턴 적용 확인용
    
        switch (mainRole.toLowerCase()) {
            case "user":
                return new UserDashboard(subRole, userId, socket, out);
            case "admin":
                return new AdminDashboard();
            default:
                throw new IllegalArgumentException("알 수 없는 역할: " + mainRole);
        }
    }
}
