package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CategoryJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class CategoryEntity implements Serializable {
    private UUID id;

    private String name;

    private String username;

    private boolean archived;

    public static CategoryEntity fromJson(CategoryJson json) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(json.id());
        categoryEntity.setName(json.name());
        categoryEntity.setUsername(json.username());
        categoryEntity.setArchived(json.archived());
        return categoryEntity;
    }
}
