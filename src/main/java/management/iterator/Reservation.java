/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.iterator;

/**
 *
 * @author suk22
 */
public class Reservation {

    private final String rawLine;
    private final String[] data;

    public Reservation(String rawLine) {
        this.rawLine = rawLine;
        this.data = rawLine.split(",");
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getStudentId() {
        return data[2];
    }

    public String getStatus() {
        return data[12];
    }

    public String getName() {
        return data[0];
    }

    public String getDepartment() {
        return data[3];
    }

    public String getRoomType() {
        return data[5];
    }

    public String getRoomNumber() {
        return data[6];
    }
}
