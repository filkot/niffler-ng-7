package guru.qa.niffler.page;

import guru.qa.niffler.page.component.PeopleTable;
import guru.qa.niffler.page.component.SearchField;

public class AllPeoplePage extends BasePage<AllPeoplePage> {
    private final SearchField searchField = new SearchField();
    private final PeopleTable peopleTable = new PeopleTable();

    public PeopleTable peopleTable() {
        return peopleTable;
    }
}
