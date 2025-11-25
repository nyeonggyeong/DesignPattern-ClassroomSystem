/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

/**
 *
 * @author user
 */
import javax.swing.*;
import java.awt.*;

public class FindPasswordView extends JFrame {

    private JTextField userNameField = new JTextField(15);
    private JTextField userIdField = new JTextField(15);

    private JCheckBox userCheck = new JCheckBox("사용자", true);
    private JCheckBox professorCheck = new JCheckBox("교수");
    private JCheckBox adminCheck = new JCheckBox("관리자");

    private JButton confirmButton = new JButton("확인");
    private JButton cancelButton = new JButton("취소");

    public JLabel labelName = new JLabel("이름:");
    public JLabel labelId = new JLabel("ID:");

    public FindPasswordView() {
        setTitle("비밀번호 찾기");
        // 세로 길이를 300으로 늘려 버튼이 잘리지 않도록 함
        setSize(350, 300); 
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // 1. 이름 필드
        labelName.setBounds(50, 30, 60, 30);
        userNameField.setBounds(100, 30, 160, 30);

        // 2. 아이디 필드
        labelId.setBounds(50, 70, 60, 30);
        userIdField.setBounds(100, 70, 160, 30);

        // 3. 역할 선택 체크박스 (y=110)
        userCheck.setBounds(70, 110, 70, 20);
        professorCheck.setBounds(140, 110, 60, 20);
        adminCheck.setBounds(200, 110, 70, 20);

        // 체크박스 그룹화 (하나만 선택)
        ButtonGroup group = new ButtonGroup();
        group.add(userCheck);
        group.add(professorCheck);
        group.add(adminCheck);

        // 4. 버튼 배치 (y값을 170으로 내려서 체크박스와 간격 확보)
        confirmButton.setBounds(60, 170, 100, 30);
        cancelButton.setBounds(170, 170, 100, 30);

        // 컴포넌트 추가
        add(labelName);
        add(userNameField);
        add(labelId);
        add(userIdField);
        
        add(userCheck);
        add(professorCheck);
        add(adminCheck);
        
        add(confirmButton);
        add(cancelButton);
    }

    public String getUserName() {
        return userNameField.getText().trim();
    }

    public String getUserId() {
        return userIdField.getText().trim();
    }

    public String getRole() {
        if (professorCheck.isSelected()) {
            return "professor";
        } else if (adminCheck.isSelected()) {
            return "admin";
        } else {
            return "user";
        }
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}