package guru.qa.niffler.test.grpc;

import guru.qa.grpc.userdata.NifflerUserdataServiceGrpc;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected static final Channel channelCurrency = ManagedChannelBuilder
            .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
            .usePlaintext()
            .intercept(new GrpcConsoleInterceptor())
            .build();


    protected static final Channel channelUserdata = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .usePlaintext()
            .intercept(new GrpcConsoleInterceptor())
            .build();

    protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub CURRENCY_SERVICE_BLOCKING_STUB
            = NifflerCurrencyServiceGrpc.newBlockingStub(channelCurrency);

    protected static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub USERDATA_SERVICE_BLOCKING_STUB
            = NifflerUserdataServiceGrpc.newBlockingStub(channelUserdata);

}
