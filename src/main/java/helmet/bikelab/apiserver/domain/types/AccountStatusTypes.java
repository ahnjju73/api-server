package helmet.bikelab.apiserver.domain.types;

public enum AccountStatusTypes {
    PENDING("100-001"), ACTIVATED("100-002"), DEACTIVATED("100-003");

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
