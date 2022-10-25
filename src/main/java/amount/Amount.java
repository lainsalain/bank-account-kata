package amount;

import exceptions.NegativeAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Amount implements Comparable<Amount> {

    final private BigDecimal amount;

    private Amount(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Amount amountOf(BigDecimal amount) throws NegativeAmountException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new NegativeAmountException();
        return new Amount(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount1 = (Amount) o;
        return Objects.equals(amount, amount1.amount);
    }


    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    public String amountValueToString(){
        return this.amount.toString();
    }

    public Amount plus(Amount amount) {
        return new Amount(this.amount.add(amount.amount));
    }

    public Amount minus(Amount amount) throws NegativeAmountException {
        if(this.amount.subtract(amount.amount).compareTo(BigDecimal.ZERO) < 0) throw new NegativeAmountException();
        return new Amount(this.amount.subtract(amount.amount));
    }

    @Override
    public int compareTo(Amount o) {
        return this.amount.compareTo(o.amount);
    }
}
