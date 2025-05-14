package me.darkakyloff.subscriptionservice.repository;

import me.darkakyloff.subscriptionservice.dto.TopSubscriptionDto;
import me.darkakyloff.subscriptionservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>
{
    List<Subscription> findAllByUserId(Long userId);

    @Query("SELECT new me.darkakyloff.subscriptionservice.dto.TopSubscriptionDto(s.name, s.provider, COUNT(s)) " + "FROM Subscription s GROUP BY s.name, s.provider ORDER BY COUNT(s) DESC")
    List<TopSubscriptionDto> findTopSubscriptions();
}