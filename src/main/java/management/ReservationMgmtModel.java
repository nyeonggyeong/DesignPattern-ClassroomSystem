/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import management.state.ReservationContext;

/**
 *
 * @author suk22
 */
public class ReservationMgmtModel {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String name;
    private String studentId;
    private String department;
    private String room;
    private String date;
    private String time;

    // 🔥 State 패턴 적용
    private ReservationContext context;

    public ReservationMgmtModel(String name, String studentId, String department,
            String room, String date, String time, String approved) {

        this.name = name;
        this.studentId = studentId;
        this.department = department;
        this.room = room;
        this.date = date;
        this.time = time;

        // 🔥 approved 문자열 → State context로 변환
        this.context = new ReservationContext(approved);
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getDepartment() {
        return department;
    }

    public String getRoom() {
        return room;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    // 🔥 상태를 문자열로 반환 (State 내부에서 관리)
    public String getApproved() {
        return context.getStatusName();
    }

    // 🔥 State 기반 승인 처리
    public void approve() {
        String old = getApproved();
        context.approve();
        pcs.firePropertyChange("approvalChanged", old, getApproved());
    }

    // 🔥 State 기반 거절 처리
    public void reject() {
        String old = getApproved();
        context.reject();
        pcs.firePropertyChange("approvalChanged", old, getApproved());
    }

    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public void setPending() {
        String old = getApproved();
        context.setState(context.getPendingState());
        pcs.firePropertyChange("approvalChanged", old, getApproved());
    }
}
