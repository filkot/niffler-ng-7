package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
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

    private static final Config CFG = Config.getInstance();


    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        String username = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        String username = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriendsPage()
                .declineFriendInvitation()
                .checkAlertMessage("Invitation of " + username + " is declined")
                .checkAmountOfFriends(0);
    }
}