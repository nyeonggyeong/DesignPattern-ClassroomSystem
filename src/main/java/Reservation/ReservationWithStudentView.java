/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 *
 * @author user
 */
public class ReservationWithStudentView extends JFrame {
    private ReservationWithStudentController controller;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel selectedCountLabel;
    private JButton confirmButton;
    private JButton cancelButton;
    
    public ReservationWithStudentView() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("세미나 학생 지정");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // 상단 안내 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("세미나에 참여할 학생을 선택하세요.");
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        topPanel.add(infoLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // 중앙 테이블 패널
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"선택", "학번", "이름", "학과"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 체크박스만 편집 가능
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(50);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        // 체크박스 변경 이벤트
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                boolean selected = (Boolean) tableModel.getValueAt(row, 0);
                String studentId = (String) tableModel.getValueAt(row, 1);
                String studentName = (String) tableModel.getValueAt(row, 2);
                String dept = (String) tableModel.getValueAt(row, 3);
                
                if (selected) {
                    controller.addSelectedStudent(studentId, studentName, dept);
                } else {
                    controller.removeSelectedStudent(studentId);
                }
                updateSelectedCount();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // 하단 버튼 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedCountLabel = new JLabel("선택된 학생: 0명");
        selectedCountLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        countPanel.add(selectedCountLabel);
        bottomPanel.add(countPanel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton("예약 확정");
        cancelButton = new JButton("취소");
        
        confirmButton.addActionListener(e -> controller.confirmReservation());
        cancelButton.addActionListener(e -> controller.cancel());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void setController(ReservationWithStudentController controller) {
        this.controller = controller;
    }
    
    public void updateStudentList(List<String[]> students) {
        tableModel.setRowCount(0);
        for (String[] student : students) {
            tableModel.addRow(new Object[]{false, student[0], student[1], student[2]});
        }
    }
    
    private void updateSelectedCount() {
        int count = controller.getSelectedStudents().size();
        selectedCountLabel.setText("선택된 학생: " + count + "명");
    }
    
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
