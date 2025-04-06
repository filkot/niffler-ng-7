package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.userdata.*;
import guru.qa.niffler.data.FriendshipEntity;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.grpc.CurrencyValues;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@GrpcService
public class GrpcUserdataService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private final UserRepository userRepository;

    public GrpcUserdataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getFriendsPageable(PageableRequest request, StreamObserver<UserPageResponse> responseObserver) {
        try {
            Page<UserWithStatus> friends = userRepository.findFriends(
                    getRequiredUser(request.getUsername()),
                    PageRequest.of(request.getPage(), request.getSize())
            );
            responseObserver.onNext(toUserPageResponse(friends));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void searchFriends(SearchFriendsRequest request, StreamObserver<UserPageResponse> responseObserver) {
        try {
            Page<UserWithStatus> friends = userRepository.findFriends(
                    getRequiredUser(request.getUsername()),
                    request.getSearchQuery(),
                    PageRequest.of(request.getPage(), request.getSize())
            );
            responseObserver.onNext(toUserPageResponse(friends));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    @Transactional
    public void sendFriendshipInvite(FriendshipInviteRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String targetUsername = request.getFriendUsername();

            // Проверка на самозапрос
            if (username.equals(targetUsername)) {
                throw new IllegalArgumentException("Can't create friendship request for self user");
            }

            // Получаем пользователей (можно оптимизировать через @EntityGraph)
            UserEntity currentUser = getRequiredUser(username);
            UserEntity targetUser = getRequiredUser(targetUsername);

            // Создаем запрос дружбы
            currentUser.addFriends(FriendshipStatus.PENDING, targetUser);
            userRepository.save(currentUser);

            // Формируем ответ
            UserEntity userEntity = getRequiredUser(username);

            FriendshipStatus friendshipStatus = userEntity.getFriendshipRequests().stream()
                    .filter(f -> targetUsername.equals(f.getAddressee().getUsername()))
                    .map(FriendshipEntity::getStatus)
                    .findFirst() // возвращает Optional<FriendshipStatus>
                    .orElseThrow(() -> new NotFoundException("Can't find friendshipStatus for user with " + username));

            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(getRequiredUser(targetUsername))
                    )
                    .setStatus(guru.qa.grpc.userdata.FriendshipStatus.valueOf(friendshipStatus.name()))
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            // Правильная обработка ошибок gRPC
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Transactional
    @Override
    public void acceptFriendship(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String friendUsername = request.getFriendUsername();

            // Проверка на самозапрос дружбы
            if (username.equals(friendUsername)) {
                throw new IllegalArgumentException("Can't accept friendship request for self user");
            }

            UserEntity currentUser = getRequiredUser(username);
            UserEntity targetUser = getRequiredUser(friendUsername);

            // Поиск существующего приглашения
            FriendshipEntity invite = currentUser.getFriendshipAddressees().stream()
                    .filter(fe -> fe.getRequester().equals(targetUser))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Can't find invitation from username: '" + friendUsername + "'"
                    ));

            // Обновление статуса и сохранение
            invite.setStatus(FriendshipStatus.ACCEPTED);
            currentUser.addFriends(FriendshipStatus.ACCEPTED, targetUser);
            userRepository.save(currentUser);

            // Получаем пользователей и проверяем их существование
            UserEntity currentUserAfterAction = getRequiredUser(username);

            UserEntity targetUserAfterAction = getRequiredUser(friendUsername);

            // Получаем статус дружбы с оптимизацией через EntityGraph
            FriendshipStatus friendshipStatus = currentUserAfterAction.getFriendshipRequests().stream()
                    .filter(f -> targetUserAfterAction.equals(f.getAddressee()))
                    .map(FriendshipEntity::getStatus)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                            String.format("No friendship request found from %s to %s", username, friendUsername)
                    ));

            // Конвертируем статус в gRPC enum
            guru.qa.grpc.userdata.FriendshipStatus grpcStatus =
                    guru.qa.grpc.userdata.FriendshipStatus.valueOf(friendshipStatus.name());

            // Формируем и отправляем ответ
            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(targetUserAfterAction))
                    .setStatus(grpcStatus)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }


    @Transactional
    public void declineFriendship(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String targetUsername = request.getFriendUsername();

            // Проверка на самозапрос
            if (username.equals(targetUsername)) {
                throw new IllegalArgumentException("Can't decline friendship request for self user");
            }

            UserEntity currentUser = getRequiredUser(username);
            UserEntity targetUser = getRequiredUser(targetUsername);

            // Запоминаем исходные размеры списков
            int initialInvitesCount = currentUser.getFriendshipAddressees().size();
            int initialFriendsCount = currentUser.getFriendshipRequests().size();

            // Двустороннее удаление связей
            currentUser.removeInvites(targetUser);
            targetUser.removeFriends(currentUser);

            // Сохраняем изменения
            userRepository.saveAll(List.of(currentUser, targetUser));

            // Проверяем фактическое удаление связей
            boolean invitesRemoved = currentUser.getFriendshipAddressees().size() < initialInvitesCount;
            boolean friendsRemoved = currentUser.getFriendshipRequests().size() < initialFriendsCount;

            // Определяем статус ответа
            guru.qa.grpc.userdata.FriendshipStatus responseStatus =
                    (invitesRemoved || friendsRemoved)
                            ? guru.qa.grpc.userdata.FriendshipStatus.DECLINED
                            : guru.qa.grpc.userdata.FriendshipStatus.UNSPECIFIED;

            // Формируем ответ
            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(targetUser))
                    .setStatus(responseStatus)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void removeFriendship(FriendshipRequest request, StreamObserver<Empty> responseObserver) {
        try {
            String username = request.getUsername();
            String targetUsername = request.getFriendUsername();

            // Проверка на самозапрос
            if (username.equals(targetUsername)) {
                throw new IllegalArgumentException("Can't remove friendship relation for self user");
            }

            // Загрузка пользователей
            UserEntity currentUser = getRequiredUser(username);
            UserEntity targetUser = getRequiredUser(targetUsername);

            // Двустороннее удаление всех связей
            currentUser.removeFriends(targetUser);
            currentUser.removeInvites(targetUser);
            targetUser.removeFriends(currentUser);
            targetUser.removeInvites(currentUser);

            // Пакетное сохранение
            userRepository.saveAll(List.of(currentUser, targetUser));

            // Отправка пустого ответа
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            // Правильная обработка ошибок gRPC
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }


    private UserPageResponse toUserPageResponse(Page<UserWithStatus> page) {
        return UserPageResponse.newBuilder()
                .setTotalElements((int) page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setFirst(page.isFirst())
                .setLast(page.isLast())
                .setSize(page.getSize())
                .addAllEdges(page.getContent().stream()
                        .map(this::toUserResponse)
                        .toList())
                .build();
    }

    private UserResponse toUserResponse(UserWithStatus user) {
        if (user == null) {
            return UserResponse.getDefaultInstance();
        }

        UserResponse.Builder builder = UserResponse.newBuilder();

        // Обработка обязательных полей
        builder.setId(user.id() != null ? user.id().toString() : "");
        builder.setUsername(user.username() != null ? user.username() : "");

        // Обработка опциональных полей
        if (user.fullname() != null) {
            builder.setFullname(user.fullname());
        }

        // Обработка currency с проверкой на null
        if (user.currency() != null) {
            try {
                builder.setCurrency(CurrencyValues.valueOf(user.currency().name()));
            } catch (IllegalArgumentException e) {
                builder.setCurrency(CurrencyValues.UNSPECIFIED);
            }
        } else {
            builder.setCurrency(CurrencyValues.UNSPECIFIED);
        }

        // Обработка photoSmall с проверкой на null
        if (user.photoSmall() != null) {
            builder.setPhotoSmall(new String(user.photoSmall(), StandardCharsets.UTF_8));
        }

        // Обработка friendshipStatus с проверкой на null
        if (user.status() != null) {
            try {
                builder.setFriendshipStatus(guru.qa.grpc.userdata.FriendshipStatus.valueOf(user.status().name()));
            } catch (IllegalArgumentException e) {
                builder.setFriendshipStatus(guru.qa.grpc.userdata.FriendshipStatus.UNSPECIFIED);
            }
        }

        return builder.build();
    }

    private UserResponse toUserResponse(UserEntity user) {
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId() != null ? user.getId().toString() : "")
                .setUsername(user.getUsername() != null ? user.getUsername() : "");

        // Обработка fullname
        if (user.getFullname() != null && !user.getFullname().isEmpty()) {
            builder.setFullname(user.getFullname());
        } else {
            builder.clearFullname(); // или setFullname("")
        }

        // Обработка currency
        if (user.getCurrency() != null) {
            builder.setCurrency(CurrencyValues.valueOf(user.getCurrency().name()));
        } else {
            builder.setCurrency(CurrencyValues.UNSPECIFIED); // или другое значение по умолчанию
        }

        // Обработка photoSmall (если нужно)
        if (user.getPhotoSmall() != null && user.getPhotoSmall().length > 0) {
            builder.setPhotoSmall(new String(user.getPhotoSmall(), StandardCharsets.UTF_8));
        } else {
            builder.clearPhotoSmall(); // или setPhotoSmall("")
        }

        return builder.build();
    }

    @Nonnull
    private UserEntity getRequiredUser(@Nonnull String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}