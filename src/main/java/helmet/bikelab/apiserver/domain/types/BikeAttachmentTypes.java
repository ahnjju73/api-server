package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeAttachmentTypes {

    COMPLETION("0", "신고필증"),
    REVOCATION("1", "폐지증명서"),
    PRODUCTION("2", "제작증")
    ;

    private String attachmentType;
    private String attachmentTypeName;

    BikeAttachmentTypes(String attachmentType, String attachmentTypeName) {
        this.attachmentType = attachmentType;
        this.attachmentTypeName = attachmentTypeName;
    }

    public static BikeAttachmentTypes getBikeAttachmentTypes(String status) {
        if (status == null) {
            return null;
        }

        for (BikeAttachmentTypes ls : BikeAttachmentTypes.values()) {
            if (status.equals(ls.getAttachmentType())) {
                return ls;
            }
        }

        return null;
    }
}
