/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;


/**
 *
 * @author adsd3
 */
 //다이얼로그 순차 흐름 제어 (블록→방종류→호수→시간대)

import java.time.LocalDate;
import java.util.List;

public class DialogController {
    private final ReservationServiceModel service;
    private final CalendarView view;

    public DialogController(ReservationServiceModel service, CalendarView view) {
        this.service = service;
        this.view = view;
    }

    /**
     * 날짜 더블클릭 시 호출되는 진입점.
     * BlockTypeDialogView에서 사용자의 선택에 따라
     * 알맞은 BlockStrategy(ConcreteStrategy)를 생성해서 실행한다.
     */
    public void handleDate(LocalDate date) {
        BlockTypeDialogView blockDlg = new BlockTypeDialogView();
        boolean isFull = service.isFullyBlocked(date.toString());
        blockDlg.setDisablePartialBlock(isFull);

        blockDlg.setHandler(new BlockTypeDialogView.Handler() {
            @Override public void onBlockFull() {
                BlockStrategy strategy =
                        new FullBlockStrategy(service, view);   // 전략 선택
                strategy.handle(date);                         // 전략 실행
                blockDlg.close();
            }

            @Override public void onBlockPartial() {
                BlockStrategy strategy =
                        new PartialBlockStrategy(service, view); // 전략 선택
                strategy.handle(date);                           // 전략 실행
                blockDlg.close();
            }

            @Override public void onUnblock() {
                BlockStrategy strategy =
                        new UnblockStrategy(service, view);    // 전략 선택
                strategy.handle(date);                         // 전략 실행
                blockDlg.close();
            }

            @Override public void onCancel() {
                blockDlg.close();
            }
        });

        blockDlg.show();
    }
}