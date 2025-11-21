/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 *
 * @author suk22
 */
public class ReservationMgmtModel {

    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this); // subject

    private String name;
    private String studentId;
    private String department;
    private String room;
    private String date;
    private String time;
    private String approved;

    public ReservationMgmtModel(String name, String studentId, String department, String room, String date, String time, String approved) {
        this.name = name;
        this.studentId = studentId;
        this.department = department;
        this.room = room;
        this.date = date;
        this.time = time;
        this.approved = approved;
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

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {

        String old = this.approved;
        if ((old == null && approved == null) || (old != null && old.equals(approved))) {
            return;
        }
        this.approved = approved;
       pcs.firePropertyChange("approvalChanged", old, approved); // 예약 승인 상태가 바뀌면 옵저버에게 알림
    }
    
    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

}
