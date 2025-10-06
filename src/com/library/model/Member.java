package com.library.model;

public class Member extends Person {
    public static final int MaxLoanLimit = 5;
    private  int activeLoanCount = 0;

    public Member(int id, String name) {
        super(id, name);
    }

    public int getActiveLoanCount() {
        return activeLoanCount;
    }

    public boolean canBorrow() {
        return activeLoanCount < MaxLoanLimit;
    }

    public void incLoan() {
        activeLoanCount++;
    }

    public void decLoan() {
        if (activeLoanCount > 0) {
            activeLoanCount--;
        }
    }
}
