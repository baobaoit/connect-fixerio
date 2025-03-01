package com.example.demo_fixerio.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

@Entity
@Table(name = "exchange_rate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"date"})
public class ExchangeRatesEntity implements Serializable, Cloneable {
    @Id
    private String date;

    private double aed;
    private double chf;
    private double eur;
    private double gbp;
    private double usd;
    private double zar;

    public void setCurrencyRates(Map<String, Double> currencyRates) throws IllegalAccessException {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldNameUpperCase = field.getName().toUpperCase();
            if (currencyRates.containsKey(fieldNameUpperCase)) {
                field.set(this, currencyRates.get(fieldNameUpperCase));
            }
        }
    }

    @Override
    public ExchangeRatesEntity clone() {
        try {
            return (ExchangeRatesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
