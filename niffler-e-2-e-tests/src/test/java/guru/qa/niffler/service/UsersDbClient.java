package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UdUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static guru.qa.niffler.data.tpl.DataSources.dataSourceChained;


public class UsersDbClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UdUserRepository udUserRepository = new UdUserRepositoryJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();


    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    private final TransactionTemplate txTemplateWithChainedTxManager = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            dataSourceChained(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            dataSourceChained(CFG.userdataJdbcUrl())
                    )
            )
    );

    public void addIncomeInvitation(String requesterUsername, String addresseeUsername) {
        Optional<UserEntity> requesterUser = udUserRepository.findByUsername(requesterUsername);
        Optional<UserEntity> addresseeUser = udUserRepository.findByUsername(addresseeUsername);
        if(requesterUser.isPresent() && addresseeUser.isPresent()){
            udUserRepository.addInvitation(requesterUser.get(), addresseeUser.get());
        }
    }

    public void addOutcomeInvitation(String addresseeUsername, String requesterUsername) {
        Optional<UserEntity> requesterUser = udUserRepository.findByUsername(requesterUsername);
        Optional<UserEntity> addresseeUser = udUserRepository.findByUsername(addresseeUsername);
        if(requesterUser.isPresent() && addresseeUser.isPresent()){
            udUserRepository.addInvitation(addresseeUser.get(), requesterUser.get());
        }
    }

    public void addFriend(String addresseeUsername, String requesterUsername) {
        Optional<UserEntity> requesterUser = udUserRepository.findByUsername(requesterUsername);
        Optional<UserEntity> addresseeUser = udUserRepository.findByUsername(addresseeUsername);
        if(requesterUser.isPresent() && addresseeUser.isPresent()){
            udUserRepository.addFriend(addresseeUser.get(), requesterUser.get());
        }
    }




    public UserJson createCorrectUserSpringJdbc(UserJson user) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
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

            authUserRepository.create(authUser);

            return UserJson.fromEntity(
                    udUserDao.create(UserEntity.fromJson(user)), null);
        });
    }

    public UserJson createIncorrectUserSpringJdbc(UserJson user) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
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

            authUserRepository.create(authUser);

            UserEntity fromJson = UserEntity.fromJson(user);
            fromJson.setUsername(null);
            return UserJson.fromEntity(
                    udUserDao.create(fromJson), null);
        });
    }

    public UserJson createWithChainedTxManager(UserJson user) {
        return txTemplateWithChainedTxManager.execute(status -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(
                            Arrays.stream(Authority.values()).map(e -> {
                                AuthorityEntity authority = new AuthorityEntity();
                                authority.setUser(authUser);
                                authority.setAuthority(e);
                                return authority;
                            }).toList()
                    );

                    authUserRepository.create(authUser);

                    UserEntity ue = udUserDao.create(UserEntity.fromJson(user));
                    return UserJson.fromEntity(ue, null);
                }
        );
    }

    public Optional<UserEntity> findByUsername(String username) {
        return xaTxTemplate.execute(() -> udUserDao.findByUsername(username));
    }
}
