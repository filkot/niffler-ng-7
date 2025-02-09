package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.api.spend.SpendApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import java.util.*;


public class SpendAPIClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();


    @Step("Создание траты через API")
    public SpendJson createSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        return spendApiClient.createSpend(spend);
    }

    @Step("Обновление траты через API")
    public SpendJson editSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        return spendApiClient.editSpend(spend);
    }

    @Step("Поиск траты по id {id} через API")
    public Optional<SpendJson> findSpendById(UUID id) {
        throw new UnsupportedOperationException("Invalid operation for spend");
    }

    @Step("Поиск траты по username {username} через API")
    public List<SpendJson> findAllByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return spendApiClient.getAllSpends(username, null, null, null);
    }

    @Step("Удаление траты через API")
    public void deleteSpend(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("Spend cannot be null");
        }
        spendApiClient.deleteSpends(spend.username(), Collections.singletonList(spend.id().toString()));
    }

    @Step("Создание категории через API")
    public CategoryJson createCategory(CategoryJson category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return categoryApiClient.createCategory(category);
    }

    @Step("Поиск категории по username {username} и categoryName {categoryName} через API")
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

    @Step("Удаление категории через API")
    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Invalid operation for category");
    }
}
