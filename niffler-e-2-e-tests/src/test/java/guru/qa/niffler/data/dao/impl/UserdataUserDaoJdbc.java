package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\"  (username, currency, firstname, surname, full_name, photo, photo_small)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
                user.setId(generatedKey);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement usersPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                          UPDATE "user"
                            SET currency    = ?,
                                firstname   = ?,
                                surname     = ?,
                                photo       = ?,
                                photo_small = ?
                            WHERE id = ?
                        """);

             PreparedStatement friendsPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     """
                             INSERT INTO friendship (requester_id, addressee_id, status)
                             VALUES (?, ?, ?)
                             ON CONFLICT (requester_id, addressee_id)
                                 DO UPDATE SET status = ?
                             """)
        ) {
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            usersPs.executeUpdate();

            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                friendsPs.setObject(1, user.getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setString(3, fe.getStatus().name());
                friendsPs.setString(4, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }
            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }


    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\"  WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = map(rs);

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
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\"  WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = map(rs);
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
    public List<UserEntity> findAll() {
        List<UserEntity> userEntities = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" "
        )) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    UserEntity userEntity = map(rs);
                    userEntities.add(userEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntities;
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\"  WHERE username = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @NotNull
    private UserEntity map(ResultSet rs) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(rs.getObject("id", UUID.class));
        userEntity.setUsername(rs.getString("username"));
        userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        userEntity.setFullname(rs.getString("full_name"));
        userEntity.setFirstname(rs.getString("firstname"));
        userEntity.setSurname(rs.getString("surname"));
        userEntity.setPhoto(rs.getBytes("photo"));
        userEntity.setPhotoSmall(rs.getBytes("photo_small"));
        return userEntity;
    }
}
