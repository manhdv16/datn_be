package com.ptit.datn.repository;

import com.ptit.datn.domain.User;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.ptit.datn.service.dto.UserNameDTO;
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

    @Query("""
        select new com.ptit.datn.service.dto.UserNameDTO(u.id, u.login, u.fullName, u.signImage) from User u
        join ContractSignatureEntity c on c.userId = u.id where c.contractId = :contractId and u.activated = true
    """)
    List<UserNameDTO> getUserWithSignImageByContractId(@Param("contractId") Long contractId);

    @Query("""
        select new com.ptit.datn.service.dto.UserNameDTO(u.id, u.login, u.fullName, u.address, u.phoneNumber, u.cccd) from User u
        join UserBuilding ub on ub.userId = u.id where ub.buildingId = :buildingId and u.activated = true
    """)
    List<UserNameDTO> findUserByBuildingId(@Param("buildingId") Long buildingId);

    @Query("select u.id from User u where u.login = :login and u.activated = :activated")
    Long getIdByLoginAndActivated(@Param("login") String login, @Param("activated") Boolean activated);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query("select u from User u inner join UserAuthority ua on u.id = ua.userId where ua.authorityName = 'ROLE_MANAGER' and u.login like concat(:search, '%') order by u.lastModifiedDate DESC ")
    Page<User> findAllByRole(Pageable pageable, @Param("search") String search);

    @Query("select u.digitalSignature from User u where u.id = :id")
    String getDigitalSignatureByUserId(@Param("id") Long id);

    @Query("select u.publicKey from User u where u.id = :id")
    String getPublicKeyByUserId(@Param("id") Long id);

    @Query("select u from User u inner join UserBuilding ub on u.id = ub.userId where ub.buildingId = :id")
    List<User> getManagerByBuilding(@Param("id") Long id);

    @Query("SELECT u FROM User u INNER JOIN UserAuthority ua ON u.id = ua.userId LEFT JOIN UserBuilding ub ON u.id = ub.userId AND ub.buildingId = :buildingId WHERE ub.userId IS NULL AND ua.authorityName = 'ROLE_MANAGER'")
    Page<User> findAllManagerNotAssignedBuildingId(Pageable pageable,@Param("buildingId") Long buildingId);

    @Query("SELECT u FROM User u INNER JOIN UserAuthority ua ON u.id = ua.userId WHERE ua.authorityName = :roleName")
    List<User> findAllByAuthoritiesName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u INNER JOIN UserBuilding ub ON u.id = ub.userId WHERE ub.buildingId = :id")
    List<User> findAllManagerByBuildingId(Long id);

    @Query("SELECT u FROM User u INNER JOIN UserBuilding ub ON u.id = ub.userId WHERE ub.buildingId = :id")
    Page<User> findAllManagerByBuildingId(Pageable pageable, Long id);
}
