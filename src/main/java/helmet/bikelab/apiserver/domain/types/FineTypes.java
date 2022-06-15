package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum FineTypes {
    FINE("508-001", "범칙금"), PENALTY("508-002", "과태료"), INDEMNIFICATION("508-003", "면책금");

    private String typeCode;
    private String type;

    FineTypes(String typeCode, String type) {
        this.typeCode = typeCode;
        this.type = type;
    }

    public static FineTypes getFineType(String typeCode) {
        if(typeCode == null)
            return null;

        for(FineTypes ft: FineTypes.values()){
            if(typeCode.equals(ft.getTypeCode())){
                return ft;
            }
        }
        return null;
    }
}
