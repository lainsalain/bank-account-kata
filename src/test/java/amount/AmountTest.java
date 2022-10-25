package amount;

import exceptions.NegativeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static amount.Amount.amountOf;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AmountTest {

    @Test
    @DisplayName("Should create Amount object")
    void shouldCreateAmountObject() {
        BigDecimal bigDecimal = new BigDecimal("10.00");
        final Amount firstAmount = assertDoesNotThrow(() -> amountOf(bigDecimal));
        final Amount secondAmount = assertDoesNotThrow(() -> amountOf(bigDecimal));
        assertEquals(firstAmount, secondAmount);
    }

    @ParameterizedTest
    @MethodSource("bigDecimalsForExceptionProvider")
    @DisplayName("Should throw NegativeAmountException when amountOf()")
    void shouldThrowWhenNegativeAmount(BigDecimal bigDecimal) {
        Exception exception = assertThrows(NegativeAmountException.class, () -> amountOf(bigDecimal));

        String expectedMessage = "The amount value cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    private static Stream<Arguments> bigDecimalsForExceptionProvider() {
        return Stream.of(
                Arguments.of(new BigDecimal("-10.00")),
                Arguments.of(new BigDecimal("-100.00"))
        );
    }

    @Test
    @DisplayName("Should add two amounts")
    void shouldAddTwoAmounts() throws NegativeAmountException {
        final Amount firstAmount = amountOf(new BigDecimal("20.00"));
        final Amount secondAmount = amountOf(new BigDecimal("45.00"));
        final Amount expectedAmount = amountOf(new BigDecimal("65.00"));

        final Amount result = firstAmount.plus(secondAmount);

        assertEquals(expectedAmount, result);

    }

    @Test
    @DisplayName("Should subtract two amounts")
    void shouldSubtractTwoAmounts() throws NegativeAmountException {
        final Amount firstAmount = amountOf(new BigDecimal("100.00"));
        final Amount secondAmount = amountOf(new BigDecimal("45.00"));
        final Amount expectedAmount = amountOf(new BigDecimal("55.00"));

        final Amount result = firstAmount.minus(secondAmount);

        assertEquals(expectedAmount, result);
    }

    @Test
    @DisplayName("Should not return a negative amount")
    void shouldNotReturnNegativeAmount() throws NegativeAmountException {
        final Amount firstAmount = amountOf(new BigDecimal("30.00"));
        final Amount secondAmount = amountOf(new BigDecimal("45.00"));

        final Exception exception = assertThrows(NegativeAmountException.class,
                () -> firstAmount.minus(secondAmount));

        assertEquals("The amount value cannot be negative.", exception.getMessage());

    }

}