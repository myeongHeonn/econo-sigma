package sigma.chackcheck.domain.book.domain;

import jakarta.persistence.*;

import java.util.List;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String publishYear;
    // 출판사
    private String publisher;
    // 도서 빌린 횟수
    private Integer borrowCount;
    // 도서 이미지
    @Column(length = 2000)
    private String imageURL;
    // 도서 관련 정보
    private String information;
}
