package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class SpendDbClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public Optional<SpendJson> findSpendById(UUID id) {
        return xaTxTemplate.execute(() ->
                spendRepository.findById(id).map(SpendJson::fromEntity)
        );
    }

    public List<SpendJson> findAllByUsername(String username) {
        return xaTxTemplate.execute(() -> {
                    List<SpendEntity> allByUsername = spendRepository.findByUsername(username);
                    return allByUsername.stream().map(SpendJson::fromEntity).collect(Collectors.toList());
                }
        );
    }

    public void deleteSpend(SpendJson spend) {
        xaTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    spendRepository.remove(spendEntity);
                    return null;
                }
        );
    }

    public SpendJson createSpend(SpendJson spend) {
        return xaTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendRepository.create(spendEntity));
                }
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return xaTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
                }
        );
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return xaTxTemplate.execute(() -> spendRepository
                .findCategoryByUsernameAndSpendName(username, categoryName)
                .map(CategoryJson::fromEntity)

        );
    }

    public void deleteCategory(CategoryJson category) {
        xaTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    spendRepository.removeCategory(categoryEntity);
                    return null;
                }
        );
    }
}