/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.schedule;

import javax.swing.*;
import java.util.List;

/**
 *
 * @author suk22
 */
public class CourseScheduleController {

    private final CourseScheduleView view;
    private final CourseScheduleService service;

    public CourseScheduleController(CourseScheduleView view, CourseScheduleService service) {
        this.view = view;
        this.service = service;

        initListeners();
        view.setVisible(true);
    }

    private void initListeners() {
        view.setSearchAction(e -> refreshTable());
        view.setAddAction(e -> onAddSchedule());
        view.setDeleteAction(e -> onDeleteSchedule());
        view.setCloseAction(e -> view.dispose());
    }

    private void refreshTable() {
        int year = view.getSelectedYear();
        int semester = view.getSelectedSemester();
        String building = view.getSelectedBuilding();
        String room = view.getSelectedRoom();

        List<CourseScheduleModel> list = service.getSchedule(year, semester, building, room);

        view.clearTable();
        for (CourseScheduleModel m : list) {
            view.addRow(new Object[]{
                m.getYear(),
                m.getSemester(),
                m.getBuilding(),
                m.getRoom(),
                m.getDay(),
                m.getSlot(),
                m.getSubject(),
                m.getProfessor()
            });
        }
    }

    private void onAddSchedule() {
        int year = view.getSelectedYear();
        int semester = view.getSelectedSemester();
        String building = view.getSelectedBuilding();
        String room = view.getSelectedRoom();

        String day = (String) JOptionPane.showInputDialog(
                view,
                "요일을 입력하세요 (예: 월, 화, 수, 목, 금):",
                "요일 입력",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "월"
        );
        if (day == null || day.trim().isEmpty()) {
            return;
        }

        String slotStr = JOptionPane.showInputDialog(view, "교시(1~8)를 입력하세요:", "1");
        if (slotStr == null || slotStr.trim().isEmpty()) {
            return;
        }

        int slot;
        try {
            slot = Integer.parseInt(slotStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "교시는 숫자로 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subject = JOptionPane.showInputDialog(view, "과목명을 입력하세요:", "");
        if (subject == null || subject.trim().isEmpty()) {
            return;
        }

        String professor = JOptionPane.showInputDialog(view, "교수명을 입력하세요:", "");
        if (professor == null || professor.trim().isEmpty()) {
            return;
        }

        // 중복(충돌) 검사
        if (service.isConflict(year, semester, building, room, day, slot)) {
            JOptionPane.showMessageDialog(view,
                    "이미 같은 요일/교시에 강의가 등록되어 있습니다.",
                    "중복 강의",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseScheduleModel model
                = new CourseScheduleModel(year, semester,
                        building, room,
                        day, slot, subject, professor);

        boolean ok = service.addSchedule(model);
        if (!ok) {
            JOptionPane.showMessageDialog(view,
                    "강의 추가에 실패했습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view,
                    "강의가 등록되었습니다.",
                    "완료",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }

    private void onDeleteSchedule() {
        int row = view.getSelectedRowIndex();
        if (row < 0) {
            JOptionPane.showMessageDialog(view,
                    "삭제할 강의를 선택하세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int year = (int) view.getValueAt(row, 0);
        int semester = (int) view.getValueAt(row, 1);
        String building = (String) view.getValueAt(row, 2);
        String room = (String) view.getValueAt(row, 3);
        String day = (String) view.getValueAt(row, 4);
        int slot = (int) view.getValueAt(row, 5);
        String subject = (String) view.getValueAt(row, 6);
        String professor = (String) view.getValueAt(row, 7);

        int confirm = JOptionPane.showConfirmDialog(view,
                "선택한 강의를 삭제하시겠습니까?",
                "삭제 확인",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        CourseScheduleModel model
                = new CourseScheduleModel(year, semester,
                        building, room,
                        day, slot, subject, professor);
        service.removeSchedule(model);
        JOptionPane.showMessageDialog(view, "삭제되었습니다.");
        refreshTable();
    }

    public static void open() {
        CourseScheduleRepository repo = new CourseScheduleRepository();
        CourseScheduleService service = new CourseScheduleService(repo);
        CourseScheduleView view = new CourseScheduleView();
        new CourseScheduleController(view, service);
    }
}
