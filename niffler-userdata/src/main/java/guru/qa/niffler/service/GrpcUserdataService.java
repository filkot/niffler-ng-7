package guru.qa.niffler.service;

import guru.qa.grpc.userdata.*;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.grpc.CurrencyValues;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;

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
                    null,
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
    public void removeFriendship(FriendshipRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        try {
            UserEntity user = getRequiredUser(request.getUsername());
            UserEntity friend = getRequiredUser(request.getFriendUsername());

            user.removeFriends(friend);
            user.removeInvites(friend);
            userRepository.save(user);

            responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void acceptFriendship(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            UserEntity user = getRequiredUser(request.getUsername());
            UserEntity friend = getRequiredUser(request.getFriendUsername());

            user.addFriends(FriendshipStatus.ACCEPTED, friend);
            userRepository.save(user);

            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(friend))
                    .setStatus(guru.qa.grpc.userdata.FriendshipStatus.ACCEPTED)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void declineFriendship(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            UserEntity user = getRequiredUser(request.getUsername());
            UserEntity friend = getRequiredUser(request.getFriendUsername());

            user.removeInvites(friend);
            userRepository.save(user);

            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(friend))
                    .setStatus(guru.qa.grpc.userdata.FriendshipStatus.DECLINED)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void sendFriendshipInvite(FriendshipInviteRequest request, StreamObserver<FriendshipResponse> responseObserver) {
        try {
            UserEntity user = getRequiredUser(request.getUsername());
            UserEntity friend = getRequiredUser(request.getFriendUsername());

            user.addInvitations(friend);
            userRepository.save(user);

            responseObserver.onNext(FriendshipResponse.newBuilder()
                    .setUser(toUserResponse(friend))
                    .setStatus(guru.qa.grpc.userdata.FriendshipStatus.PENDING)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
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
        return UserResponse.newBuilder()
                .setId(user.id().toString())
                .setUsername(user.username())
                .setFullname(user.fullname())
                .setCurrency(CurrencyValues.valueOf(user.currency().name()))
                .setPhotoSmall(new String(user.photoSmall(), StandardCharsets.UTF_8))
                .setFriendshipStatus(guru.qa.grpc.userdata.FriendshipStatus.valueOf(user.status().name()))
                .build();
    }

    private UserResponse toUserResponse(UserEntity user) {
        return UserResponse.newBuilder()
                .setId(user.getId().toString())
                .setUsername(user.getUsername())
                .setFullname(user.getFullname())
                .setCurrency(CurrencyValues.valueOf(user.getCurrency().name()))
                .setPhotoSmall(new String(user.getPhotoSmall(), StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    private UserEntity getRequiredUser(@Nonnull String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}