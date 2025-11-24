/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CanceledReservationView extends JFrame {

    private JTable table;

    public CanceledReservationView() {
        setTitle("취소된 예약 목록");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {

        String[] columnNames = {
            "이름", "학과", "학번",
            "강의실", "날짜", "시간",
            "상태", "취소 사유"
        };

        table = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton closeButton = new JButton("닫기");

        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCanceledData();
    }

    private void loadCanceledData() {

        File file = new File("src/main/resources/cancelled_reservations.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "취소된 예약이 없습니다.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 15) {
                    System.out.println("데이터가 부족한 라인 무시: " + line);
                    continue;
                }

                String name = data[0];
                String userType = data[1];
                String studentId = data[2];
                String department = data[3];
                String building = data[4];
                String roomType = data[5];
                String roomNumber = data[6];
                String date = data[7];
                String day = data[8];
                String startTime = data[9];
                String endTime = data[10];
                String purpose = data[11];
                String approved = data[12];

                String cancelStatus = data[13];
                String reason = data[14];

                String displayName = name + "(" + userType + ")";
                String roomDisplay = building + " / " + roomNumber;
                String timeDisplay = startTime + " ~ " + endTime;

                model.addRow(new Object[]{
                    displayName, department, studentId,
                    roomDisplay, date, timeDisplay,
                    cancelStatus, reason
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
