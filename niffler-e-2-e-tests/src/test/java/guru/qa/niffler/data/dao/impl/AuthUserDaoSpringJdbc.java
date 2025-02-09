package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserDaoSpringJdbc implements AuthUserDao {
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, pe.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            return ps;

        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        int affectedRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                            "account_non_locked = ?, credentials_non_expired = ? " +
                            "WHERE id = ?");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());
            return ps;
        });
        if (affectedRows == 0) {
            throw new RuntimeException("Updating user failed, no rows affected.");
        }
        return user;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM \"user\" WHERE id = ?",
                            AuthUserEntityRowMapper.instance,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance);
    }

    @Override
    public void remove(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        int affectedRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM \"user\" WHERE id = ?");
            ps.setObject(1, user.getId());
            return ps;
        });

        if (affectedRows == 0) {
            throw new RuntimeException("Deleting user failed, no rows affected.");
        }
    }

}
