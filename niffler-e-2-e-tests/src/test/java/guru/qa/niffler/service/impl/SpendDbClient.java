package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class SpendDbClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public Optional<SpendJson> findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() ->
                        spendRepository.findById(id).map(SpendJson::fromEntity),
                Connection.TRANSACTION_SERIALIZABLE
        );
    }

    @Override
    public List<SpendJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
                    List<SpendEntity> allByUsername = spendRepository.findByUsername(username);
                    return allByUsername.stream().map(SpendJson::fromEntity).collect(Collectors.toList());
                },
                Connection.TRANSACTION_SERIALIZABLE
        );
    }

    @Override
    public void deleteSpend(SpendJson spend) {

    }


    @Override
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendRepository.create(spendEntity));
                },
                Connection.TRANSACTION_SERIALIZABLE
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
                },
                Connection.TRANSACTION_SERIALIZABLE
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() ->
                        spendRepository.findCategoryByUsernameAndSpendName(username, categoryName)
                                .map(CategoryJson::fromEntity),
                Connection.TRANSACTION_SERIALIZABLE
        );
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    spendRepository.removeCategory(categoryEntity);
                    return null;
                },
                Connection.TRANSACTION_SERIALIZABLE
        );
    }
}
