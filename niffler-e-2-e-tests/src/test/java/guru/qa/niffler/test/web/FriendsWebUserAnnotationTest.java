package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Friend;
import guru.qa.niffler.jupiter.annotation.IncomeInvitation;
import guru.qa.niffler.jupiter.annotation.OutcomeInvitation;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebUserAnnotationTest {


    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .shouldSeeEmptyTabPanelFriends();
    }

    @User(
            friends = {
                    @Friend
            }
    )
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .shouldSeeFriendInFriendsTable(
                        user.testData().friends().getFirst().username());

    }

    @User(
            incomeInvitations = {
                    @IncomeInvitation
            }
    )
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .shouldSeeFriendNameRequestInRequestsTable(
                        user.testData().incomeInvitations().getFirst().username());

    }

    @User(
            outcomeInvitations = {
                    @OutcomeInvitation
            }
    )
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openAllPeoplePage()
                .peopleTable()
                .shouldSeeOutcomeInvitationInAllPeoplesTable(
                        user.testData().outcomeInvitations().getFirst().username())
        ;
    }

    @User(
            incomeInvitations = {
                    @IncomeInvitation
            }
    )
    @Test
    void shouldBeAbleToAcceptFriendRequest(UserJson user) {
        final String username = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .acceptFriendInvitation()
                .checkAlertMessage("Invitation of " + username + " accepted")
                .checkAmountOfFriends(1);
    }

    @User(
            incomeInvitations = {
                    @IncomeInvitation
            }
    )
    @Test
    void shouldBeAbleToDeclineFriendRequest(UserJson user) {
        final String username = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .declineFriendInvitation()
                .checkAlertMessage("Invitation of " + username + " is declined")
                .checkAmountOfFriends(0);
    }

    @User(
            friends = {
                    @Friend
            }
    )
    @Test
    void shouldRemoveFriend(UserJson user) {
        final String userToRemove = user.testData().friends().getFirst().username();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .removeFriend(userToRemove)
                .checkAmountOfFriends(0);
    }
}