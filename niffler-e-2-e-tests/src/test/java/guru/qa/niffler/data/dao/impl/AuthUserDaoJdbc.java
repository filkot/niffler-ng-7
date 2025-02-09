package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, pe.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in GeneratedKeys");
                }
                user.setId(generatedKey);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" " +
                        "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? " +
                        "WHERE id = ?"
        )) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());

            ps.executeUpdate();
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity userEntity = map(rs);
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        List<AuthUserEntity> userEntities = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity userEntity = map(rs);
                    userEntities.add(userEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntities;
    }

    @Override
    public void remove(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {

            ps.setObject(1, user.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    private static @Nonnull AuthUserEntity map(ResultSet rs) throws SQLException {
        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setId(rs.getObject("id", UUID.class));
        userEntity.setUsername(rs.getString("username"));
        userEntity.setPassword(rs.getString("password"));
        userEntity.setEnabled(rs.getBoolean("enabled"));
        userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return userEntity;
    }
}
