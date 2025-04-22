package guru.qa.niffler.config;

import org.jetbrains.annotations.NotNull;

enum DockerConfig implements Config {
    INSTANCE;

    @NotNull
    @Override
    public String frontUrl() {
        return "http://frontend.niffler.dc/";
    }

    @NotNull
    @Override
    public String authUrl() {
        return "http://auth.niffler.dc:9000/";
    }

    @NotNull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://niffler-all-db:5432/niffler-auth";
    }

    @NotNull
    @Override
    public String gatewayUrl() {
        return "http://gateway.niffler.dc:8090/";
    }

    @NotNull
    @Override
    public String userdataUrl() {
        return "http://userdata.niffler.dc:8089/";
    }

    @NotNull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://niffler-all-db:5432/niffler-userdata";
    }

    @NotNull
    @Override
    public String spendUrl() {
        return "http://spend.niffler.dc:8093/";
    }

    @NotNull
    @Override
    public String spendJdbcUrl() {
        return "jdbc:postgresql://niffler-all-db:5432/niffler-spend";
    }

    @NotNull
    @Override
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://niffler-all-db:5432/niffler-currency";
    }

    @NotNull
    @Override
    public String currencyGrpcAddress() {
        return "currency.niffler.dc";
    }

    @NotNull
    @Override
    public String userdataGrpcAddress() {
        return "userdata.niffler.dc";
    }
}
