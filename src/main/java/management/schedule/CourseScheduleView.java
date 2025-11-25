/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 *
 * @author suk22
 */
public class CourseScheduleView extends JFrame {

    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> semesterCombo;
    private JComboBox<String> buildingCombo;
    private JComboBox<String> roomCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton searchButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton closeButton;

    public CourseScheduleView() {
        setTitle("강의실 강의 시간 관리");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        yearCombo = new JComboBox<>(new Integer[]{2024, 2025, 2026});
        semesterCombo = new JComboBox<>(new Integer[]{1, 2});

        buildingCombo = new JComboBox<>(new String[]{"산학협력관", "정보공학관"});
        roomCombo = new JComboBox<>();

        updateRoomComboByBuilding();

        buildingCombo.addActionListener(e -> updateRoomComboByBuilding());

        searchButton = new JButton("조회");
        addButton = new JButton("강의 추가");
        deleteButton = new JButton("선택 삭제");
        closeButton = new JButton("닫기");

        filterPanel.add(new JLabel("학년도:"));
        filterPanel.add(yearCombo);
        filterPanel.add(new JLabel("학기:"));
        filterPanel.add(semesterCombo);
        filterPanel.add(new JLabel("건물:"));
        filterPanel.add(buildingCombo);
        filterPanel.add(new JLabel("강의실:"));
        filterPanel.add(roomCombo);
        filterPanel.add(searchButton);
        filterPanel.add(addButton);
        filterPanel.add(deleteButton);
        filterPanel.add(closeButton);

        add(filterPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"학년도", "학기", "건물", "강의실", "요일", "교시", "과목명", "교수명"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 편집 불가
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateRoomComboByBuilding() {
        String building = (String) buildingCombo.getSelectedItem();
        if (building == null) {
            return;
        }

        if (building.equals("산학협력관")) {
            roomCombo.setModel(new DefaultComboBoxModel<>(new String[]{
                "401", "402", "403", "404"
            }));
        } else { // 정보공학관
            roomCombo.setModel(new DefaultComboBoxModel<>(new String[]{
                "701", "702", "911", "912", "913", "914", "915", "916", "917", "918"
            }));
        }
    }

    public int getSelectedYear() {
        return (int) yearCombo.getSelectedItem();
    }

    public int getSelectedSemester() {
        return (int) semesterCombo.getSelectedItem();
    }

    public String getSelectedBuilding() {
        return (String) buildingCombo.getSelectedItem();
    }

    public String getSelectedRoom() {
        return (String) roomCombo.getSelectedItem();
    }

    public void setSearchAction(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void setAddAction(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void setDeleteAction(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void setCloseAction(ActionListener listener) {
        closeButton.addActionListener(listener);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    public Object getValueAt(int row, int col) {
        return tableModel.getValueAt(row, col);
    }
}
