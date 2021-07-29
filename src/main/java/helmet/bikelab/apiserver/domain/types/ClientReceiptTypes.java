package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ClientReceiptTypes {

    EMAIL("EMAIL", "이메일");

    private String receiptType;
    private String receiptName;

    ClientReceiptTypes(String receiptType, String receiptName) {
        this.receiptType = receiptType;
        this.receiptName = receiptName;
    }

    public static ClientReceiptTypes getClientReceiptTypes(String session) {
        if (session == null) {
            return null;
        }

        for (ClientReceiptTypes sess : ClientReceiptTypes.values()) {
            if (session.equals(sess.getReceiptType())) {
                return sess;
            }
        }

        return null;
    }

}
