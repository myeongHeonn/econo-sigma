package sigma.chackcheck.domain.user.dto.response;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class CurruentBorrowedBooks {
    private List<CurruentBorrowedBook> curruentBorrowedBooks;
}
