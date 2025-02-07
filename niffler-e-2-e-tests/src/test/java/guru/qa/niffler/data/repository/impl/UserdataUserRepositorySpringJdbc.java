package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private final UserdataUserDao userDaoSpringJdbc = new UserdataUserDaoSpringJdbc();

    @Override
    public @Nonnull UserEntity create(UserEntity user) {
        return userDaoSpringJdbc.create(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        return userDaoSpringJdbc.findById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {
        return userDaoSpringJdbc.findByUsername(username);
    }

    @Override
    public @Nonnull UserEntity update(UserEntity user) {
        return userDaoSpringJdbc.update(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        userDaoSpringJdbc.update(requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        userDaoSpringJdbc.update(requester);
        userDaoSpringJdbc.update(addressee);
    }

    @Override
    public void remove(UserEntity user) {
        userDaoSpringJdbc.delete(user);
    }
}
