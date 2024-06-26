package sigma.chackcheck.common.presentation;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@UtilityClass
public class ApiResponseGenerator {

    public static ApiResponse<ApiResponseBody.SuccessBody<Void>> success(final HttpStatus status, SuccessMessage successMessage) {
        return new ApiResponse<>(new ApiResponseBody.SuccessBody<>(String.valueOf(status.value()), successMessage.getMessage(), null), status);
    }

    public static <D> ApiResponse<ApiResponseBody.SuccessBody<D>> success(final D data, final HttpStatus status, SuccessMessage successMessage) {
        return new ApiResponse<>(new ApiResponseBody.SuccessBody<>(String.valueOf(status.value()), successMessage.getMessage(), data), status);
    }

    public static ApiResponse<ApiResponseBody.FailureBody> fail(final HttpStatus status, final String code, final String message) {
        return new ApiResponse<>(new ApiResponseBody.FailureBody(String.valueOf(status.value()), code, message), status);
    }

    public static ApiResponse<ApiResponseBody.FailureBody> fail(BindingResult bindingResult, final HttpStatus status, final String code) {
        return new ApiResponse<>(new ApiResponseBody.FailureBody(String.valueOf(status.value()), code, createErrorMessage(bindingResult)), status);
    }

    private static String createErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            if (!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append("[");
            sb.append(fieldError.getField());
            sb.append("] ");
            sb.append(fieldError.getDefaultMessage());
        }

        return sb.toString();
    }
}
