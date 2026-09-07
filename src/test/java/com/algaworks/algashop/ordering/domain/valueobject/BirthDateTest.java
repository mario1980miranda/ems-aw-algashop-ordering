package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;

class BirthDateTest {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test
    void given_validPastDate_whenCreate_shouldKeepValue() {
        LocalDate date = LocalDate.of(1991, Month.AUGUST, 5);

        BirthDate birthDate = new BirthDate(date);

        Assertions.assertThat(birthDate.value()).isEqualTo(date);
    }

    @Test
    void given_today_whenCreate_shouldNotGenerateException() {
        LocalDate today = LocalDate.now(UTC);

        Assertions.assertThatNoException().isThrownBy(() -> new BirthDate(today));
    }

    @Test
    void given_nullDate_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new BirthDate(null));
    }

    @Test
    void given_futureDate_whenCreate_shouldGenerateException() {
        LocalDate tomorrow = LocalDate.now(UTC).plusDays(1);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(tomorrow))
                .withMessage("BirthDate must be a past date");
    }

    @Test
    void given_pastDate_whenCallAge_shouldReturnCompletedYears() {
        LocalDate date = LocalDate.now(UTC).minusYears(30);

        Assertions.assertThat(new BirthDate(date).age()).isEqualTo(30);
    }

    @Test
    void given_birthdayNotReachedThisYear_whenCallAge_shouldNotCountCurrentYear() {
        LocalDate date = LocalDate.now(UTC).minusYears(30).plusDays(1);

        Assertions.assertThat(new BirthDate(date).age()).isEqualTo(29);
    }

    @Test
    void given_knownDate_whenCallAge_shouldMatchPeriodBetween() {
        LocalDate date = LocalDate.of(1991, Month.AUGUST, 5);
        int expected = Period.between(date, LocalDate.now(UTC)).getYears();

        Assertions.assertThat(new BirthDate(date).age()).isEqualTo(expected);
    }

    @Test
    void given_birthDate_whenCallToString_shouldReturnIsoDate() {
        BirthDate birthDate = new BirthDate(LocalDate.of(1991, Month.AUGUST, 5));

        Assertions.assertThat(birthDate).hasToString("1991-08-05");
    }

    @Test
    void given_sameDate_whenCompare_shouldBeEqual() {
        LocalDate date = LocalDate.of(1991, Month.AUGUST, 5);

        Assertions.assertThat(new BirthDate(date)).isEqualTo(new BirthDate(date))
                .hasSameHashCodeAs(new BirthDate(date));
    }
}
