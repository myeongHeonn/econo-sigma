package sigma.chackcheck.domain.book.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sigma.chackcheck.common.dto.PageInfo;
import sigma.chackcheck.common.presentation.ApiResponse;
import sigma.chackcheck.common.presentation.ApiResponseBody;
import sigma.chackcheck.common.presentation.ApiResponseBody.SuccessBody;
import sigma.chackcheck.common.presentation.ApiResponseGenerator;
import sigma.chackcheck.common.presentation.SuccessMessage;
import sigma.chackcheck.domain.book.domain.Book;
import sigma.chackcheck.domain.book.domain.BookApprove;
import sigma.chackcheck.domain.book.domain.BookDetail;
import sigma.chackcheck.domain.book.dto.request.CreateBookApproveRequest;
import sigma.chackcheck.domain.book.dto.request.CreateBookRequest;
import sigma.chackcheck.domain.book.dto.request.CreateBookRequestDTO;
import sigma.chackcheck.domain.book.dto.response.BookApproveDTO;
import sigma.chackcheck.domain.book.dto.response.BookApprovePageResponse;
import sigma.chackcheck.domain.book.dto.response.BookDTO;
import sigma.chackcheck.domain.book.dto.response.BookDetailDTO;
import sigma.chackcheck.domain.book.dto.response.BookDetailPageResponse;
import sigma.chackcheck.domain.book.dto.response.BookPageResponse;
import sigma.chackcheck.domain.book.dto.response.FullBookDTO;
import sigma.chackcheck.domain.book.service.BookService;
import sigma.chackcheck.domain.bookBorrow.domain.BookBorrow;
import sigma.chackcheck.domain.bookBorrow.dto.response.BookBorrowDTO;
import sigma.chackcheck.domain.bookBorrow.service.BookBorrowService;
import sigma.chackcheck.domain.user.domain.User;
import sigma.chackcheck.domain.user.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookBorrowService bookBorrowService;
    private final UserService userService;

    @GetMapping("/all")
    public ApiResponse<SuccessBody<BookPageResponse>> getMainPage(
        @RequestParam(value = "page", defaultValue = "0") int page) {

        Page<Book> bookList = bookService.getBookPage(page);

        return getBookSuccessBodyApiResponse(page, bookList);
    }

    @GetMapping("/all/search")
    public ApiResponse<SuccessBody<BookPageResponse>> getBooksBySearch(
        @RequestParam(value = "keyword") String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page
    ){
        Page<Book> bookList = bookService.getBookPageBySearch(keyword, page);

        return getBookSuccessBodyApiResponse(page, bookList);
    }

    @GetMapping("/category")
    public ApiResponse<SuccessBody<BookPageResponse>> getBooksByCategoryName(
        @RequestParam(value = "categoryName") String categoryName,
        @RequestParam(value = "page", defaultValue = "0") int page
    ){
        Page<Book> bookList = bookService.getBookPageByCategoryName(categoryName, page);

        return getBookSuccessBodyApiResponse(page, bookList);
    }

    @GetMapping("/category/search")
    public ApiResponse<SuccessBody<BookPageResponse>> getBooksByCategoryNameAndKeyword(
        @RequestParam(value = "categoryName") String categoryName,
        @RequestParam(value = "keyword") String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page
    ){
        Page<Book> bookList = bookService.getBookPageByCategoryNameAndKeyword(categoryName,keyword, page);

        return getBookSuccessBodyApiResponse(page, bookList);
    }

    @GetMapping("/{bookId}")
    public ApiResponse<SuccessBody<BookDetailPageResponse>> getBookDetails(
        @PathVariable(value = "bookId") Long bookId
    ){
        // TODO: 리팩토링 무조건 필요함!!
        Book book = bookService.getOneBook(bookId);
        FullBookDTO fullBookDTO = FullBookDTO.of(book);

        List<BookDetail> bookDetailList = bookService.getAllBookDetailsByBookId(bookId);
        List<BookDetailDTO> bookDetailDTOList = new ArrayList<>();

        for (BookDetail bookDetail : bookDetailList) {
            Optional<BookBorrow> optionalBookBorrow = bookBorrowService.getCurrentlyBorrowedBookListByBookDetailId(
                bookDetail.getId());
            BookBorrowDTO bookBorrowDTO = null;
            if (optionalBookBorrow.isPresent()) {
                BookBorrow bookBorrow = optionalBookBorrow.get();
                User user = userService.findById(bookBorrow.getUserId());
                // TODO: dueDate 만드는 로직 추가
                // TODO: db에 반납해야하는 기간 추가
                bookBorrowDTO = BookBorrowDTO.of(bookBorrow, user.getName());
            }
            BookDetailDTO bookDetailDTO = BookDetailDTO.of(bookDetail, bookBorrowDTO);
            bookDetailDTOList.add(bookDetailDTO);
        }

        BookDetailPageResponse bookDetailPageResponse = BookDetailPageResponse.of(fullBookDTO, bookDetailDTOList);

        return ApiResponseGenerator.success(bookDetailPageResponse, HttpStatus.OK, SuccessMessage.GET);
    }

    @PostMapping()
    public ApiResponse<SuccessBody<Void>> createBookApprove(@AuthenticationPrincipal User loginUser, @RequestBody CreateBookApproveRequest createBookApproveRequest){
        bookService.createBookApprove(createBookApproveRequest, loginUser.getId());
        return ApiResponseGenerator.success(HttpStatus.CREATED, SuccessMessage.CREATE);
    }

    @DeleteMapping("/bookDetail/{bookDetailId}")
    public ApiResponse<SuccessBody<Void>> softDeleteBookDetail(
        @PathVariable(value = "bookDetailId") Long bookDetailId
    ){
        BookDetail bookDetail = bookService.getOneBookDetail(bookDetailId);
        if (bookDetail.isDeleted() || bookDetail.isBorrowStatus()) {
            throw new IllegalStateException("대출 중이거나 삭제된 도서는 삭제할 수 없습니다.");
        }
        bookService.softDeleteBookDetail(bookDetailId);
        return ApiResponseGenerator.success(HttpStatus.OK, SuccessMessage.DELETE);
    }

    private ApiResponse<SuccessBody<BookPageResponse>> getBookSuccessBodyApiResponse(
        @RequestParam(value = "page", defaultValue = "0") int page,
        Page<Book> bookList) {
        PageInfo pageInfo =  PageInfo.of(page, bookList.getTotalElements(), bookList.getTotalPages());

        List<BookDTO> bookDtoList = bookList.getContent().stream()
            .map(BookDTO::of)
            .toList();

        BookPageResponse bookPageResponse = BookPageResponse.of(pageInfo, bookDtoList);

        return ApiResponseGenerator.success(bookPageResponse, HttpStatus.OK, SuccessMessage.GET);
    }
}
