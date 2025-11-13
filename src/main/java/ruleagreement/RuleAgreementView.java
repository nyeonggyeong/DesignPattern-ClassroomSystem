/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ruleagreement;

/**
 *
 * @author adsd3
<<<<<<< HEAD
 * @author 염승욱
 * @modify 강의실 사용 규칙 모두 동의 생성
 * @since 2025-11-11
=======
>>>>>>> 515b6c9566f2d3d10fa02317d91c3b890a906328
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RuleAgreementView extends JFrame {
    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final JButton nextButton = new JButton("다음");
    private JButton allCheckButton; 
    
    public RuleAgreementView(List<String> rules) {
        setTitle("강의실 사용 규칙 동의");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));

        for (String rule : rules) {
            JCheckBox box = new JCheckBox(rule);
            checkBoxes.add(box);
            checkPanel.add(box);
        }

        // 모두 동의 버튼 구현
        allCheckButton = new JButton("모두 동의");
        
        allCheckButton.addActionListener(e -> {
            checkBoxes.forEach(
                    box -> box.setSelected(true)
            );
        });
        
        checkPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 규칙과 버튼 사이에 여백
        checkPanel.add(allCheckButton);
        

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("모든 강의실 사용 규칙에 동의해주세요."), BorderLayout.NORTH);
        panel.add(new JScrollPane(checkPanel), BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.SOUTH);

        add(panel);
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public boolean allChecked() {
        return checkBoxes.stream().allMatch(AbstractButton::isSelected);
    }
    
}