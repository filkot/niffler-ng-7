package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        AuthUserEntity authUserEntity = authUserDao.create(user);
        authAuthorityDao.create(user.
                getAuthorities().toArray(new AuthorityEntity[0]));
        return authUserEntity;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        AuthUserEntity updatedUser = authUserDao.update(user);
        authAuthorityDao.remove(user.getAuthorities().getFirst());
        authAuthorityDao.create(user.
                getAuthorities().toArray(new AuthorityEntity[0]));
        return updatedUser;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.query(
                """
                        SELECT a.id as authority_id,
                        authority,
                        user_id as id,
                        u.username,
                        u.password,
                        u.enabled,
                        u.account_non_expired,
                        u.account_non_locked,
                        u.credentials_non_expired
                        FROM "user" u
                        JOIN public.authority a
                        ON u.id = a.user_id
                        WHERE u.id = ?
                        """,
                AuthUserEntityResultSetExtractor.instance,
                id
        ));
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void remove(AuthUserEntity user) {
        authAuthorityDao.remove(user.getAuthorities().getFirst());
        authUserDao.remove(user);
    }

}
