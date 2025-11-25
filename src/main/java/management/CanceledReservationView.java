/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import management.iterator.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CanceledReservationView extends JFrame {

    private JTable table;

    public CanceledReservationView() {
        setTitle("취소된 예약 목록");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {

        String[] cols = {
            "이름", "학과", "학번", "강의실",
            "날짜", "시간", "상태", "취소 사유"
        };

        table = new JTable(new DefaultTableModel(cols, 0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        JPanel p = new JPanel();
        p.add(closeBtn);
        add(p, BorderLayout.SOUTH);

       loadCanceledData();
    }

    private void loadCanceledData() {

        ReservationCollection canceled
                = new ReservationCollection("src/main/resources/cancelled_reservations.txt");

        Iterator<Reservation> it = canceled.createIterator();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        while (it.hasNext()) {
            Reservation r = it.next();
            String[] data = r.getRawLine().split(",");

            if (data.length < 15) {
                continue;
            }

            String displayName = data[0] + "(" + data[1] + ")";
            String roomDisplay = data[4] + " / " + data[6];
            String timeDisplay = data[9] + " ~ " + data[10];

            model.addRow(new Object[]{
                displayName,
                data[3],
                data[2],
                roomDisplay,
                data[7],
                timeDisplay,
                data[13],
                data[14]
            });
        }
    }
}
