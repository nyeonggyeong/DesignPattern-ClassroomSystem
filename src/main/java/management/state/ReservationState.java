/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public interface ReservationState {
    void approve(ReservationContext ctx);
    void reject(ReservationContext ctx);
    String getStatusName();
}
