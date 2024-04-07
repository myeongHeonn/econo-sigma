package sigma.chackcheck.domain.user;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;
    private String loginId;
    private String password;
    // 기수
    private Integer grade;
    // 대출/예약 유무
    private Boolean isRentReserve;
    // 대출 도서 개수
    private Integer borrowCount;
    // 예약 도서 개수
    private Integer reserveCount;
}
