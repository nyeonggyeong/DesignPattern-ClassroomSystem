/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author suk22
 */
public class ReservationObserverPatternTest {

    private ReservationMgmtDataModel subject;
    private ReservationMgmtObserver mockObserver1;
    private ReservationMgmtObserver mockObserver2;
    private ReservationMgmtObserver mockObserver3;

    @BeforeEach
    public void setUp() {
        subject = new ReservationMgmtDataModel();
        mockObserver1 = mock(ReservationMgmtObserver.class);
        mockObserver2 = mock(ReservationMgmtObserver.class);
        mockObserver3 = mock(ReservationMgmtObserver.class);
    }

    //Observer 등록 테스트 
    @Nested
    @DisplayName("Observer 등록")
    class RegisterObserverTests {

        @Test
        @DisplayName("단일 Observer를 Subject에 등록할 수 있다")
        public void testRegisterSingleObserver() {
            // When
            subject.registerObserver(mockObserver1);

            // Then
            assertTrue(subject.observers.contains(mockObserver1),
                    "Observer가 Subject의 리스트에 추가되어야 함");
            assertEquals(1, subject.observers.size());
        }

        @Test
        @DisplayName("여러 개의 Observer를 등록할 수 있다")
        public void testRegisterMultipleObservers() {
            // When
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);
            subject.registerObserver(mockObserver3);

            // Then
            assertEquals(3, subject.observers.size());
            assertTrue(subject.observers.contains(mockObserver1));
            assertTrue(subject.observers.contains(mockObserver2));
            assertTrue(subject.observers.contains(mockObserver3));
        }

        @Test
        @DisplayName("null Observer는 등록되지 않는다")
        public void testRegisterNullObserverNotAllowed() {
            // When
            subject.registerObserver(null);

            // Then
            assertEquals(0, subject.observers.size(),
                    "null Observer는 등록되지 않아야 함");
        }

        @Test
        @DisplayName("동일한 Observer는 중복 등록되지 않는다")
        public void testDuplicateObserverNotRegistered() {
            // When
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver1);

            // Then
            assertEquals(1, subject.observers.size(),
                    "동일한 Observer는 한 번만 등록되어야 함");
        }
    }

    // Observer 제거 테스트 
    @Nested
    @DisplayName("Observer 제거")
    class RemoveObserverTests {

        @Test
        @DisplayName("등록된 Observer를 제거할 수 있다")
        public void testRemoveRegisteredObserver() {
            // Given
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);

            // When
            subject.removeObserver(mockObserver1);

            // Then
            assertEquals(1, subject.observers.size());
            assertFalse(subject.observers.contains(mockObserver1));
            assertTrue(subject.observers.contains(mockObserver2));
        }

        @Test
        @DisplayName("등록되지 않은 Observer를 제거해도 에러가 발생하지 않는다")
        public void testRemoveUnregisteredObserverDoesNotCrash() {
            // When & Then
            assertDoesNotThrow(() -> {
                subject.removeObserver(mockObserver1);
            });
            assertEquals(0, subject.observers.size());
        }

        @Test
        @DisplayName("모든 Observer를 제거할 수 있다")
        public void testRemoveAllObservers() {
            // Given
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);
            subject.registerObserver(mockObserver3);

            // When
            subject.removeObserver(mockObserver1);
            subject.removeObserver(mockObserver2);
            subject.removeObserver(mockObserver3);

            // Then
            assertEquals(0, subject.observers.size());
        }
    }

    // 알림 테스트
    @Nested
    @DisplayName("Observer 알림")
    class NotifyObserversTests {

        @Test
        @DisplayName("모든 등록된 Observer가 알림을 받는다")
        public void testAllObserversReceiveNotification() {
            // Given
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);
            subject.registerObserver(mockObserver3);

            // When
            subject.notifyObservers();

            // Then
            verify(mockObserver1, times(1)).update();
            verify(mockObserver2, times(1)).update();
            verify(mockObserver3, times(1)).update();
        }

        @Test
        @DisplayName("제거된 Observer는 알림을 받지 않는다")
        public void testRemovedObserverDoesNotReceiveNotification() {
            // Given
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);
            subject.removeObserver(mockObserver1);

            // When
            subject.notifyObservers();

            // Then
            verify(mockObserver1, never()).update();
            verify(mockObserver2, times(1)).update();
        }

        @Test
        @DisplayName("Observer가 없으면 알림을 보내지 않는다 (에러 없음)")
        public void testNotifyObserversWithNoObservers() {
            // When & Then
            assertDoesNotThrow(() -> {
                subject.notifyObservers();
            });
        }

        @Test
        @DisplayName("여러 번 알림을 보낼 수 있다")
        public void testMultipleNotifications() {
            // Given
            subject.registerObserver(mockObserver1);

            // When
            subject.notifyObservers();
            subject.notifyObservers();
            subject.notifyObservers();

            // Then
            verify(mockObserver1, times(3)).update();
        }

        @Test
        @DisplayName("등록 후 즉시 알림을 받을 수 있다")
        public void testObserverReceivesNotificationImmediatelyAfterRegistration() {
            // When
            subject.registerObserver(mockObserver1);
            subject.notifyObservers();

            // Then
            verify(mockObserver1, times(1)).update();
        }
    }

    @Nested
    @DisplayName("Observer 생명주기")
    class ObserverLifecycleTests {

        @Test
        @DisplayName("Observer 등록 → 알림 → 제거 → 알림 시나리오")
        public void testObserverLifecycle() {
            // 1단계: Observer 등록
            subject.registerObserver(mockObserver1);
            subject.notifyObservers();
            verify(mockObserver1, times(1)).update();

            // 2단계: Observer 제거
            subject.removeObserver(mockObserver1);

            // 3단계: 알림 전송 (제거된 Observer는 받지 않음)
            subject.notifyObservers();
            verify(mockObserver1, times(1)).update();
        }

        @Test
        @DisplayName("동적 Observer 추가/제거 시나리오")
        public void testDynamicObserverAdditionAndRemoval() {
            // 초기 상태: Observer1 등록
            subject.registerObserver(mockObserver1);
            subject.notifyObservers();
            verify(mockObserver1, times(1)).update();

            // 상황 1: Observer2, Observer3 추가
            subject.registerObserver(mockObserver2);
            subject.registerObserver(mockObserver3);
            subject.notifyObservers();
            verify(mockObserver1, times(2)).update();
            verify(mockObserver2, times(1)).update();
            verify(mockObserver3, times(1)).update();

            // 상황 2: Observer2 제거
            subject.removeObserver(mockObserver2);
            subject.notifyObservers();
            verify(mockObserver1, times(3)).update();
            verify(mockObserver2, times(1)).update(); // 여전히 1번
            verify(mockObserver3, times(2)).update();
        }

        @Test
        @DisplayName("null과 중복을 포함한 등록 시나리오")
        public void testObserverRegistrationWithNullAndDuplicates() {
            // When
            subject.registerObserver(mockObserver1);
            subject.registerObserver(null);
            subject.registerObserver(mockObserver1);
            subject.registerObserver(mockObserver2);
            subject.registerObserver(null);

            // Then
            assertEquals(2, subject.observers.size(),
                    "null과 중복을 제외하면 2개의 Observer만 등록되어야 함");
            assertTrue(subject.observers.contains(mockObserver1));
            assertTrue(subject.observers.contains(mockObserver2));
        }

        @Test
        @DisplayName("Observer 상태 변경 추적 테스트")
        public void testObserverStateChangeTracking() {
            // Given
            subject.registerObserver(mockObserver1);

            // When - 여러 번의 상태 변경 발생
            for (int i = 0; i < 5; i++) {
                subject.notifyObservers();
            }

            // Then
            verify(mockObserver1, times(5)).update();
            System.out.println(" 5번의 상태 변경 각각에 대해 Observer에 알림이 전달되었습니다.");
        }

        @Test
        @DisplayName("Subject 상태 변경 시 등록된 Observer에게 알림을 보낸다")
        public void testNotifyObserverWhenStateChanges() {
            // Given
            subject.registerObserver(mockObserver1);

            // When
            subject.notifyObservers();

            // Then
            verify(mockObserver1, times(1)).update();
        }

        @Test
        @DisplayName("Observer 제거 후 즉시 알림을 받지 않는다")
        public void testObserverDoesNotReceiveNotificationAfterRemoval() {
            // Given
            subject.registerObserver(mockObserver1);
            subject.removeObserver(mockObserver1);

            // When
            subject.notifyObservers();

            // Then
            verify(mockObserver1, never()).update();
        }
    }
}
