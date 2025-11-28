/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.iterator;

/**
 *
 * @author suk22
 */
public class ReservationCollection {

    private final String filePath;

    public ReservationCollection(String filePath) {
        this.filePath = filePath;
    }

    public Iterator<Reservation> createIterator() {
        return new ReservationIterator(filePath);
    }
}


