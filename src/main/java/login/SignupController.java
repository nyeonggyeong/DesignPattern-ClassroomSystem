package login;

import ServerClient.SocketManager;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SignupController {
    private final SignupView view;
    private final SignupModel model;

    public SignupController(SignupView view, SignupModel model) {
        this.view = view;
        this.model = model;

        // “등록” 버튼 클릭 시 sendRegister() 실행
        view.btnRegister.addActionListener(this::sendRegister);

        // “뒤로가기” 버튼 클릭 시 로그인 화면으로 돌아가기
        view.btnBack.addActionListener(e -> {
            view.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginView loginView  = new LoginView();
                LoginModel loginModel = new LoginModel();
                new LoginController(loginView, loginModel);
                loginView.setLocationRelativeTo(null);
                loginView.setVisible(true);
            });
        });
    }

    private void sendRegister(ActionEvent e) {
        String id   = view.getId();
        String pw   = view.getPw();
        String name = view.getName();
        String dept = view.getDept();
        String role = view.getRole(); // 예: “USER” 또는 “ADMIN”

        // 빈칸 체크
        if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(view, "모든 항목을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Socket 가져오기
        Socket socket = SocketManager.getSocket();
        if (socket == null || socket.isClosed()) {
            JOptionPane.showMessageDialog(view, "서버에 연결되어 있지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // REGISTER 프로토콜 전송
            String msg = String.join(":", "REGISTER", role, id, pw, name, dept);
            out.write(msg);
            out.newLine();
            out.flush();

            // 서버 응답 처리
            String response = in.readLine();
            if ("REGISTER_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(view, "회원가입 되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);

                // ---------------- Adapter 적용 ----------------
                IUser user = new UserImpl(id, role, name, dept);
                IUserAdapter adapter = new UserAdapter(user);

                // 테스트용 출력
                adapter.showInfo();
                System.out.println("Role: " + adapter.getRole());
                // ---------------------------------------------

                view.dispose();

                // 회원가입 완료 후 로그인 화면으로
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    LoginModel loginModel = new LoginModel();
                    new LoginController(loginView, loginModel);
                    loginView.setLocationRelativeTo(null);
                    loginView.setVisible(true);
                });

            } else {
                JOptionPane.showMessageDialog(view, "회원가입에 실패하였습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "서버 통신 오류: " + ex.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }
}
