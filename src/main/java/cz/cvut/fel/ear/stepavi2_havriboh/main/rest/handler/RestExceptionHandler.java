package cz.cvut.fel.ear.stepavi2_havriboh.main.rest.handler;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(RuntimeException ex) {
        LOG.error("Exception caught:", ex);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return new ErrorInfo(e.getMessage(), request.getRequestURI());
    }

    // Persistence-related exceptions

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorInfo> handlePersistenceException(HttpServletRequest request, PersistenceException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Not found exceptions
    @ExceptionHandler({
            AccountNotFoundException.class,
            BudgetNotFoundException.class,
            CategoryNotFoundException.class,
            TransactionNotFoundException.class,
            UserNotFoundException.class,
            ReportNotFoundException.class
    })

    public ResponseEntity<ErrorInfo> handleNotFoundException(HttpServletRequest request, RuntimeException e) {
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.NOT_FOUND);
    }

    // Validation-related exceptions
    @ExceptionHandler({
            EmailAlreadyTakenException.class,
            UsernameAlreadyTakenException.class,
            InvalidDateException.class,
            InvalidTransactionTypeException.class,
            EmptyNameException.class,
            EmptyCurrencyException.class,
            EmptyDescriptionException.class,
            EmptyReportTypeException.class
    })

    public ResponseEntity<ErrorInfo> handleValidationException(HttpServletRequest request, RuntimeException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    // Limit-related exceptions
    @ExceptionHandler({
            BudgetLimitExceededException.class,
            CategoryLimitExceededException.class,
            NegativeCategoryLimitException.class,
            RemoveMoreThanCurrentBudgetException.class
    })

    public ResponseEntity<ErrorInfo> handleLimitException(HttpServletRequest request, RuntimeException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.CONFLICT);
    }

    // Subscription and user-related exceptions
    @ExceptionHandler({
            NotPremiumUserException.class,
            SubscriptionNotActiveException.class,
            UserAlreadyInAccountException.class,
            LastUserInAccountException.class
    })

    public ResponseEntity<ErrorInfo> handleUserException(HttpServletRequest request, RuntimeException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.CONFLICT);
    }

    // Financial exceptions
    @ExceptionHandler({
            NegativeAmountException.class,
            NegativeBalanceException.class,
            TargetAmountException.class
    })

    public ResponseEntity<ErrorInfo> handleFinancialException(HttpServletRequest request, RuntimeException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.CONFLICT);
    }
}
