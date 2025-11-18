package login;

import ServerClient.FileWatcher;
import ServerClient.SocketManager;
import dashboard.Dashboard;
import dashboard.DashboardFactory;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class LoginController {

    private final LoginView view;
    private final LoginModel model;
    private final Socket socket;
    private final BufferedWriter out;
    private final BufferedReader in;

    public LoginController(LoginView view, LoginModel model) {
        this.view = view;
        this.model = model;

        Socket tempSocket = SocketManager.getSocket();
        BufferedWriter tempOut = null;
        BufferedReader tempIn = null;

        if (tempSocket == null || tempSocket.isClosed()) {
            JOptionPane.showMessageDialog(view, "서버에 연결되어 있지 않습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
            this.socket = null;
            this.out = null;
            this.in = null;
            return;
        }

        try {
            tempOut = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
            tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "스트림 생성 실패: " + e.getMessage(), "스트림 오류", JOptionPane.ERROR_MESSAGE);
            SocketManager.close();
            this.socket = null;
            this.out = null;
            this.in = null;
            return;
        }

        this.socket = tempSocket;
        this.out = tempOut;
        this.in = tempIn;
        setupListeners();
    }

    private void setupListeners() {
        view.getLoginButton().addActionListener(e -> attemptLogin());
        view.getRegisterButton().addActionListener(e -> handleSignup());
    }

    private void attemptLogin() {
        String userId = view.getUserId();
        String password = view.getPassword();
        String role = view.getRole(); // student / professor / admin

        try {
            out.write("LOGIN:" + userId + "," + password + "," + role);
            out.newLine();
            out.flush();

            String response = in.readLine();

            if ("LOGIN_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(view, userId + "님 로그인 성공");

                SocketManager.setSocket(socket);
                new FileWatcher().start();

                // 서버에 유저 정보 요청
                out.write("INFO_REQUEST:" + userId + "\n");
                out.flush();

                String userInfoResponse = in.readLine();
                String userType = role;

                if (userInfoResponse != null && userInfoResponse.startsWith("INFO_RESPONSE:")) {
                    String[] parts = userInfoResponse.substring("INFO_RESPONSE:".length()).split(",");
                    if (parts.length >= 4) {
                        userType = parts[3]; // student / professor / admin
                    }
                }

                // ✅ 팩토리 패턴 적용
                String mainRole = "admin".equalsIgnoreCase(userType) ? "admin" : "user";
                String subRole = "admin".equalsIgnoreCase(userType) ? null : userType;

                Dashboard dashboard = DashboardFactory.createDashboard(mainRole, subRole, userId, socket, out);
                dashboard.show();

                view.dispose();

            } else if ("WAIT".equals(response)) {
                JOptionPane.showMessageDialog(view, "현재 접속 인원 초과로 대기 중입니다.");
            } else {
                JOptionPane.showMessageDialog(view, "로그인 실패");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "서버 통신 오류: " + ex.getMessage());
        }
    }

    public void handleSignup() {
        view.dispose();
        SignupView signupView = new SignupView();
        SignupModel signupModel = new SignupModel();
        new SignupController(signupView, signupModel);
        signupView.setVisible(true);
    }
}
