package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ShopStatusTypes {
    ACTIVATE("701-001"), PENDING("701-002"), DEACTIVATE("701-003");

    private String shopStatusTeyp;

    ShopStatusTypes(String shopStatusTeyp) {
        this.shopStatusTeyp = shopStatusTeyp;
    }

    public static ShopStatusTypes getShopStatusTypes(String shopStatusTeyp) {
        if (shopStatusTeyp == null) {
            return null;
        }

        for (ShopStatusTypes as : ShopStatusTypes.values()) {
            if (shopStatusTeyp.equals(as.getShopStatusTeyp())) {
                return as;
            }
        }

        return null;
    }

}
