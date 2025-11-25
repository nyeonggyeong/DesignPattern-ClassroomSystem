/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

/**
 *
 * @author suk22
 */
import java.util.ArrayList;
import java.util.List;

public abstract class ReservationMgmtSubject {

    protected List<ReservationMgmtObserver> observers = new ArrayList<>();

    public void registerObserver(ReservationMgmtObserver o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(ReservationMgmtObserver o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        for (ReservationMgmtObserver o : observers) {
            o.update();
        }
    }
}
