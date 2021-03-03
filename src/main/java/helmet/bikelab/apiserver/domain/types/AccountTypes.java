package helmet.bikelab.apiserver.domain.types;

public enum AccountTypes {
    COMPLETE("101-001"), PENDING("101-002"), INPROGRESS("101-003"), DELETED("101-004");

    private String type;

    public static AccountTypes getType(String type){
        if(type == null){
            return null;
        }
        for(AccountTypes at: AccountTypes.values()){
            if(type.equals(at.getType())){
                return at;
            }
        }
        return null;
    }

    AccountTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
