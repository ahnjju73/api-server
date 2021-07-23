package helmet.bikelab.apiserver.domain.types;

public enum AccountTypes {

    EMAIL("001-001"), GOOGLE("001-002"), FACEBOOK("001-003"), KAKAO("001-004");

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
