package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity user) {
        AuthUserEntity authUserEntity = authUserDao.create(user);
        authAuthorityDao.create(user.
                getAuthorities().toArray(new AuthorityEntity[0]));
        return authUserEntity;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity user) {
        AuthUserEntity updatedUser = authUserDao.update(user);
        authAuthorityDao.remove(user.getAuthorities().getFirst());
        authAuthorityDao.create(user.
                getAuthorities().toArray(new AuthorityEntity[0]));
        return updatedUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT a.id as authority_id,\n" +
                        " authority,\n" +
                        " user_id as id,\n" +
                        " u.username,\n" +
                        " u.password,\n" +
                        " u.enabled,\n" +
                        " u.account_non_expired,\n" +
                        " u.account_non_locked,\n" +
                        " u.credentials_non_expired\n" +
                        "FROM \"user\" u join public.authority a on u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityResultSetExtractor.instance.extractData(rs);
                    }

                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setUser(user);
                    authority.setId(rs.getObject("a.id", UUID.class));
                    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(authority);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void remove(AuthUserEntity user) {
        authAuthorityDao.remove(user.getAuthorities().getFirst());
        authUserDao.remove(user);
    }

}
