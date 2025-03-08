package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.jupiter.convector.Browser.chromeConfig;


public class FriendsWebTest {

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(chromeConfig);

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        browserExtension.addDriver(chrome);
        final String friendUsername = user.testData().friendsUsernames()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .checkExistingFriends(friendUsername);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        browserExtension.addDriver(chrome);
        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .checkExistingFriendsCount(0);
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        browserExtension.addDriver(chrome);
        final String incomeInvitationUsername = user.testData().incomeInvitationsUsernames()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .checkExistingInvitations(incomeInvitationUsername);
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        browserExtension.addDriver(chrome);
        final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toAllPeoplesPage()
                .checkInvitationSentToUser(outcomeInvitationUsername);
    }

    @User(friends = 1)
    @Test
    void shouldRemoveFriend(UserJson user) {
        browserExtension.addDriver(chrome);
        final String userToRemove = user.testData().friendsUsernames()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .removeFriend(userToRemove)
                .checkExistingFriendsCount(0);
    }

    @User(incomeInvitations = 1)
    @Test
    void shouldAcceptInvitation(UserJson user) {
        browserExtension.addDriver(chrome);
        final String userToAccept = user.testData().incomeInvitationsUsernames()[0];

        chrome.open(LoginPage.URL);
        FriendsPage friendsPage = new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .checkExistingInvitationsCount(1)
                .acceptFriendInvitationFromUser(userToAccept)
                .checkExistingInvitationsCount(0);

        chrome.refresh();

        friendsPage.checkExistingFriendsCount(1)
                .checkExistingFriends(userToAccept);
    }

    @User(incomeInvitations = 1)
    @Test
    void shouldDeclineInvitation(UserJson user) {
        browserExtension.addDriver(chrome);
        final String userToDecline = user.testData().incomeInvitationsUsernames()[0];

        chrome.open(LoginPage.URL);
        FriendsPage friendsPage = new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toFriendsPage()
                .checkExistingInvitationsCount(1)
                .declineFriendInvitationFromUser(userToDecline)
                .checkExistingInvitationsCount(0);

        chrome.refresh();

        friendsPage.checkExistingFriendsCount(0);

        chrome.open(PeoplePage.URL);
        new PeoplePage()
                .checkExistingUser(userToDecline);
    }
}
