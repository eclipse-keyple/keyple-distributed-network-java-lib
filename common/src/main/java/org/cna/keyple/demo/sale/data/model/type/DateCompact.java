package org.cna.keyple.demo.sale.data.model.type;

import java.util.Objects;

/**
 * Hold number of days since 1st jan 2010
 * Dates are in legal local time
 */
public class DateCompact {

    private short daysSinceReference;

    public DateCompact(short daysSinceReference) {
        this.daysSinceReference = daysSinceReference;
    }

    /**
     * Get number of days since 1st jan 2010
     * @return short
     */
    public short getDaysSinceReference() {
        return daysSinceReference;
    }

    public void setDaysSinceReference(short daysSinceReference) {
        this.daysSinceReference = daysSinceReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateCompact)) return false;
        DateCompact that = (DateCompact) o;
        return getDaysSinceReference() == that.getDaysSinceReference();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDaysSinceReference());
    }

    @Override
    public String toString() {
        return "DateCompact{" +
                "daysSinceReference=" + daysSinceReference +
                '}';
    }
}
