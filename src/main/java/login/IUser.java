/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package login;

/**
 *
 * @author USER
 */
public interface IUser {
    String getId();
    String getName();
    String getDept();
    String getRole();  // student / professor / admin
}
