package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConvectorFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.userdata.wsdl.*;
import io.qameta.allure.Step;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConvectorFactory.create("niffler-userdata"));
        userdataApi = create(UserdataSoapApi.class);
    }

    private <T> T executeSoapCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("SOAP request failed with code: " + response.code());
        }
        return response.body();
    }

    @Step("Get current user's info using SOAP API")
    public @Nonnull UserResponse currentUser(String username) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(username);
        return executeSoapCall(userdataApi.currentUser(request));
    }

    @Step("Update user info using SOAP API")
    public @Nonnull UserResponse updateUser(User user) throws IOException {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUser(user);
        return executeSoapCall(userdataApi.updateUser(request));
    }

    @Step("Get all users with pagination using SOAP API")
    public @Nonnull UsersResponse allUsers(String username, int page, int size) throws IOException {
        AllUsersPageRequest request = new AllUsersPageRequest();
        request.setUsername(username);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        request.setPageInfo(pageInfo);

        return executeSoapCall(userdataApi.allUsers(request));
    }

    @Step("Get all users with search query using SOAP API")
    public @Nonnull UsersResponse allUsersWithSearch(String username, String searchQuery) throws IOException {
        AllUsersRequest request = new AllUsersRequest();
        request.setUsername(username);
        request.setSearchQuery(searchQuery);
        return executeSoapCall(userdataApi.allUsersWithSearch(request));
    }

    @Step("Get friends list using SOAP API")
    public @Nonnull UsersResponse friends(String username) throws IOException {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(username);
        return executeSoapCall(userdataApi.friends(request));
    }

    @Step("Get friends list with pagination using SOAP API")
    public @Nonnull UsersResponse friendsWithPagination(String username, int page, int size, @Nullable String searchQuery) throws IOException {
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(username);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        request.setPageInfo(pageInfo);

        if (searchQuery != null) {
            request.setSearchQuery(searchQuery);
        }

        return executeSoapCall(userdataApi.friendsWithSearch(request));
    }

    @Step("Send friend invitation using SOAP API")
    public @Nonnull UserResponse sendInvitation(String username, String friendUsername) throws IOException {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeRequested(friendUsername);
        return executeSoapCall(userdataApi.sendInvitation(request));
    }

    @Step("Accept friend invitation using SOAP API")
    public @Nonnull UserResponse acceptInvitation(String username, String friendUsername) throws IOException {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeAdded(friendUsername);
        return executeSoapCall(userdataApi.acceptInvitation(request));
    }

    @Step("Decline friend invitation using SOAP API")
    public @Nonnull UserResponse declineInvitation(String username, String friendUsername) throws IOException {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(username);
        request.setInvitationToBeDeclined(friendUsername);
        return executeSoapCall(userdataApi.declineInvitation(request));
    }

    @Step("Remove friend using SOAP API")
    public void removeFriend(String username, String friendUsername) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(username);
        request.setFriendToBeRemoved(friendUsername);
        executeSoapCall(userdataApi.removeFriend(request));
    }
}
