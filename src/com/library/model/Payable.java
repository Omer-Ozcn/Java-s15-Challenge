package com.library.model;

import java.math.BigDecimal;

public interface Payable {
    BigDecimal amount();
    String description();
}
