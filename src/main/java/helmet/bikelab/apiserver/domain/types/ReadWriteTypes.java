package helmet.bikelab.apiserver.domain.types;

public enum ReadWriteTypes {
    READONLY("002-001"), WRTING("002-002");

    private String auth;

    ReadWriteTypes(String auth) {
        this.auth = auth;
    }

    public static ReadWriteTypes getAuth(String authorization) {
        if (authorization == null) {
            return null;
        }

        for (ReadWriteTypes auth : ReadWriteTypes.values()) {
            if (authorization.equals(auth.getAuth())) {
                return auth;
            }
        }

        return null;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
