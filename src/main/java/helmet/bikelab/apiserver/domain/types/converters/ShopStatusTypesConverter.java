package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ShopStatusTypes;

import javax.persistence.AttributeConverter;

public class ShopStatusTypesConverter implements AttributeConverter<ShopStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(ShopStatusTypes status) {
        return status.getShopStatusTeyp();
    }

    @Override
    public ShopStatusTypes convertToEntityAttribute(String status) {
        return ShopStatusTypes.getShopStatusTypes(status);
    }
}
