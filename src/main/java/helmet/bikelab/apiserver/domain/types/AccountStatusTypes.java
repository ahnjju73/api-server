package helmet.bikelab.apiserver.domain.types;

public enum AccountStatusTypes {
    PENDING("201-002"), COMPLETED("201-001"), IN_PROGRESS("201-003"), DELETE("201-004");

    private String accountStatus;

    AccountStatusTypes(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public static AccountStatusTypes getAccountStatus(String accountStatus) {
        if (accountStatus == null) {
            return null;
        }

        for (AccountStatusTypes as : AccountStatusTypes.values()) {
            if (accountStatus.equals(as.getAccountStatus())) {
                return as;
            }
        }

        return null;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
