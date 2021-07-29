package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ClientReceiptTypes;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;

import javax.persistence.AttributeConverter;

public class ClientReceiptTypesConverter implements AttributeConverter<ClientReceiptTypes, String> {
    @Override
    public String convertToDatabaseColumn(ClientReceiptTypes clientReceiptTypes) {
        return clientReceiptTypes.getReceiptType();
    }

    @Override
    public ClientReceiptTypes convertToEntityAttribute(String session) {
        return ClientReceiptTypes.getClientReceiptTypes(session);
    }
}
