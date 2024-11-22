package com.ptit.datn.repository;

import com.ptit.datn.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByActivationKey(String activationKey);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<User> findOneByResetKey(String resetKey);
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByLogin(String login);
    Optional<User> findOneById(Long id);

    @Query("select u.id from User u where u.login = :login and u.activated = :activated")
    Long getIdByLoginAndActivated(@Param("login") String login, @Param("activated") Boolean activated);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query("select u from User u inner join UserAuthority ua on u.id = ua.userId where ua.authorityName = 'ROLE_MANAGER'")
    Page<User> findAllByRole(Pageable pageable);

    @Query("select u.digitalSignature from User u where u.id = :id")
    String getDigitalSignatureByUserId(@Param("id") Long id);

    @Query("select u from User u inner join UserBuilding ub on u.id = ub.userId where ub.buildingId = :id")
    List<User> getManagerByBuilding(@Param("id") Long id);
}
