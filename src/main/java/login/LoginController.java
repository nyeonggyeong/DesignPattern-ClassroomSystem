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
//    private Thread listener; //알람 수신 스레드
    
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

                SocketManager.setSocket(socket);  // ← 이 줄을 꼭 먼저 추가
                
                checkCancelNotification();

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

                Dashboard dashboard = DashboardFactory.createDashboard(mainRole, subRole, userId, socket, out, in);
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

    public void checkCancelNotification() {
        try {
            while (in.ready()) {
                String message = in.readLine();
                if (message != null && message.startsWith("CANCEL_NOTIFICATION:")) {
                    String data = message.substring("CANCEL_NOTIFICATION:".length());
                    showNotification(data);
                }
            }
        } catch (IOException e) {
            System.out.println("에러: " + e.getMessage());
        }
    }
    //"%s,%s,%s,%s-%s",name,id,room,startTime,endTime
    public void showNotification(String data) {
        if (data != null && !data.isEmpty()) {
            String[] notifications = data.split(";");
            
            StringBuilder sb = new StringBuilder();
            sb.append("교수님의 예약으로 인해 다음 예약이 취소되었습니다.\n");
            for (String notification : notifications) {
                String[] parts = notification.split(",");
                sb.append(String.format("%s님의 %s\n",parts[0],parts[2]));
                sb.append(String.format("시간: %s",parts[3]));
                JOptionPane.showMessageDialog(null, sb.toString(), "예약 취소 알림",JOptionPane.WARNING_MESSAGE);
            }
            
            
        }
    }
    
    private void pollingNotification() {
        int delay = 500;
        
        Timer timer = new Timer(delay, e-> {
            checkCancelNotification();
        });
        
        timer.setRepeats(true);
        timer.start();
    }
    
//    private void notificationListener() {
//        listener = new Thread(() -> {
//            try {
//                String msg;
//                while ((msg = in.readLine()) != null) {
//                    if(msg.startsWith("CANCEL_NOTIFICATION:")) {
//                        String data = msg.substring("CANCEL_NOTIFICATION:".length());
//                        showNotification(data);
//                    }
//                }
//            } catch (IOException e) {
//                System.out.println("에러: " + e.getMessage());
//            }
//        });
//        listener.setDaemon(true);
//        listener.start();
//    }

}
