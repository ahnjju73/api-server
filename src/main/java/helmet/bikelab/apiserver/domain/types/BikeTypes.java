package helmet.bikelab.apiserver.domain.types;

public enum BikeTypes {
    GAS("507-001"), ELECTRONIC("507-002");

    private String type;

    BikeTypes(String type){
        this.type = type;
    }

    public static BikeTypes getType(String type){
        if(type == null){
            return null;
        }
        for(BikeTypes bt : BikeTypes.values()){
            if(type.equals(bt.getType())){
                return bt;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    private void setType(String type){
        this.type = type;
    }

    public String getBikeTypeUnit(){
        if(BikeTypes.GAS.equals(this)){
            return "cc";
        }else if(BikeTypes.ELECTRONIC.equals(this)){
            return "kw";
        }
        return "";
    }

}
