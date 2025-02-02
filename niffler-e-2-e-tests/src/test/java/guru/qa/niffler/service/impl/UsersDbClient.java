package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.getRandomUsername;


public class UsersDbClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository udUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username, password);

            authUserRepository.create(authUser);

            return UserJson.fromEntity(
                    udUserRepository.create(userEntity(username)), null);
        });
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(targetUser.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    String username = getRandomUsername();
                    String password = "12345";

                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    UserEntity addressee = udUserRepository.create(userEntity(username));

                    udUserRepository.sendInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(targetUser.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    String username = getRandomUsername();
                    String password = "12345";

                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    UserEntity addressee = udUserRepository.create(userEntity(username));

                    udUserRepository.sendInvitation(addressee, targetEntity);
                    return null;
                });
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(targetUser.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    String username = getRandomUsername();
                    String password = "12345";

                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    UserEntity addressee = udUserRepository.create(userEntity(username));

                    udUserRepository.addFriend(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(e -> {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setUser(authUser);
                    authority.setAuthority(e);
                    return authority;
                }).toList()
        );
        return authUser;
    }
}