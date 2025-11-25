/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
/**
 *
 * @author user
 */
public class FindPasswordController {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private LoginView parentView;
    private FindPasswordView view = new FindPasswordView();
    
    public FindPasswordController(Socket socket, BufferedWriter out, BufferedReader in, LoginView view) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.parentView = view;
        
        this.view.setVisible(true);
        setListeners();
    }
    
    private void setListeners() {
        view.getConfirmButton().addActionListener(e -> sendMessage());
        view.getCancelButton().addActionListener(e -> back());
    }
    
    private void sendMessage() {
        String userId = view.getUserId();
        String userName = view.getUserName();
        String userRole = view.getRole();
        
        if (userId.isEmpty() || userId == null || 
                userName.isEmpty() || userName == null || 
                userRole.isEmpty() || userRole == null) {
            JOptionPane.showMessageDialog(view, "이름, 아이디, 역할 모두 입력해주세요.");
            return;
        }
        
        StringBuilder sb = new StringBuilder("FIND_PASSWORD:");
        sb.append(userId);
        sb.append(",");
        sb.append(userName);
        sb.append(",");
        sb.append(userRole);
        
        System.out.println("입력값:" + sb.toString());
        try {
            out.write(sb.toString());
            out.newLine();
            out.flush();
            System.out.println("전송 완료");
            String response = in.readLine();
            System.out.println("서버 응답: " + response);
            if (response.startsWith("FIND_PASSWORD:")) {
                String pwd = response.substring("FIND_PASSWORD:".length());
                if (pwd.equals("NOT_FOUND")) {
                    JOptionPane.showMessageDialog(view, "찾으시는 비밀번호는 등록되어있지 않습니다.");
                    return;
                } else {
                    JOptionPane.showMessageDialog(view, "비밀번호: " + pwd);
                }                
            } 
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
        }
        
        parentView.setVisible(true);
        view.dispose();
    }
    
    public void back() {
        view.dispose();
        parentView.setVisible(true);
    }
}
