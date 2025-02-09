package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserEntityResultSetExtractor implements ResultSetExtractor<AuthUserEntity> {

    public static final AuthUserEntityResultSetExtractor instance = new AuthUserEntityResultSetExtractor();

    private AuthUserEntityResultSetExtractor() {
    }

    @Override
    public @Nonnull AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userMap = new HashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                AuthUserEntity userEntity = new AuthUserEntity();
                userEntity.setId(id);
                try {
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setPassword(rs.getString("password"));
                    userEntity.setEnabled(rs.getBoolean("enabled"));
                    userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return userEntity;
            });
            AuthorityEntity authority = new AuthorityEntity();
            authority.setId(rs.getObject("authority_id", UUID.class));
            authority.setAuthority(Authority.valueOf(rs.getString("authority")));
            user.getAuthorities().add(authority);
        }
        return userMap.get(userId);
    }
}
