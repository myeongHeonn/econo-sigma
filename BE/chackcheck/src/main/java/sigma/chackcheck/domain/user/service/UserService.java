package sigma.chackcheck.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigma.chackcheck.domain.book.domain.BookDetail;
import sigma.chackcheck.domain.book.repository.BookDetailRepository;
import sigma.chackcheck.domain.bookBorrow.domain.BookBorrow;
import sigma.chackcheck.domain.bookBorrow.repository.BookBorrowRepository;
import sigma.chackcheck.domain.user.domain.User;
import sigma.chackcheck.domain.user.dto.request.AddUserRequest;
import sigma.chackcheck.domain.user.dto.response.BookRentInfoResponse;
import sigma.chackcheck.domain.user.dto.response.BookRentInfosResponse;
import sigma.chackcheck.domain.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BookBorrowRepository bookBorrowRepository;
    private final BookDetailRepository bookDetailRepository;

    public User findById(Long Id) {
        return userRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + Id));
    }

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }

    public void updatePassword(String loginId, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public BookRentInfosResponse getBorrowHistory(Long userId) {
        List<BookBorrow> borrowHistory = bookBorrowRepository.findByUserId(userId);
        List<BookRentInfoResponse> bookRentInfos = borrowHistory.stream()
                .map(borrow -> {
                    BookDetail bookDetail = bookDetailRepository.findById(borrow.getBookDetailId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid book detail ID"));
                    return BookRentInfoResponse.from(borrow, bookDetail.getTitle());
                })
                .collect(Collectors.toList());
        return new BookRentInfosResponse(bookRentInfos);
    }
}
