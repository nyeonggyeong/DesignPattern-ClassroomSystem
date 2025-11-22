/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package login;

/**
 *
 * @author USER
 */
public interface IUserAdapter {
     void showInfo();      // 사용자 정보 출력(테스트용/화면 출력용)
    String getId();
    String getName();
    String getDept();
    String getRole();
}
