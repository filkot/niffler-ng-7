package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.api.spend.SpendApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.*;


public class SpendAPIClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();


    public SpendJson createSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        return spendApiClient.createSpend(spend);
    }

    public SpendJson editSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        return spendApiClient.editSpend(spend);
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        throw new UnsupportedOperationException("Invalid operation for spend");
    }

    public List<SpendJson> findAllByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return spendApiClient.getAllSpends(username);
    }

    public void deleteSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        spendApiClient.deleteSpends(spend.username(), Collections.singletonList(spend.id().toString()));
    }

    public CategoryJson createCategory(CategoryJson category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return categoryApiClient.createCategory(category);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        if (username == null || categoryName == null) {
            throw new IllegalArgumentException("Username and categoryName cannot be null");
        }
        List<CategoryJson> allCategories = new ArrayList<>(categoryApiClient.getAllCategories(username, true));
        allCategories.addAll(categoryApiClient.getAllCategories(username, false));
        return allCategories.stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();
    }

    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Invalid operation for category");
    }
}
